package org.shypl.tool.utils.keeper

import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.assistant.TemporalAssistant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.function.BiFunction
import kotlin.concurrent.Volatile

abstract class AbstractKeeper<I : Any, E : Any, M : E, R : M>(
	private val assistant: TemporalAssistant,
	private val lifetime: Long,
	private val lifetimeUnit: TimeUnit,
) : Keeper<I, E, M> {
	
	private val entities = ConcurrentHashMap<I, Entry<R>>()
	
	init {
		require(lifetime > 0)
	}
	
	protected abstract fun load(id: I): R
	
	protected abstract fun save(id: I, entity: R)
	
	protected abstract fun dispose(id: I, entity: R)
	
	protected abstract fun handleError(message: String, error: Throwable? = null)
	
	override fun touch(id: I): R? {
		return entities.computeIfPresent(id, touchMapper)?.entity
	}
	
	private val touchMapper = BiFunction { id: I, entry: Entry<R>? ->
		entry?.also {
			if (it.holds == 0) scheduleDispose(id, it)
		}
	}
	
	override fun get(id: I): R {
		return entities.compute(id, getMapper)?.entity ?: throw RuntimeException("Fail to get entity (id: $id)")
	}
	
	private val getMapper = BiFunction { id: I, entry: Entry<R>? ->
		entry?.also {
			if (it.holds == 0) scheduleDispose(id, it)
		} ?: try {
			Entry(load(id)).also { scheduleDispose(id, it) }
		}
		catch (e: Throwable) {
			handleError("Fail to load entity (id: $id)", e)
			null
		}
	}
	
	final override fun hold(id: I): R {
		return entities.compute(id, holdMapper)?.entity ?: throw RuntimeException("Fail to hold entity (id: $id)")
	}
	
	private val holdMapper = BiFunction { id: I, entry: Entry<R>? ->
		entry?.also {
			if (synchronized(it) { ++it.holds } == 1) {
				it.killer.cancel()
				it.killer = Cancelable.DUMMY
			}
		} ?: try {
			Entry(load(id)).also { it.holds = 1 }
		}
		catch (e: Throwable) {
			handleError("Fail to load entity (id: $id)", e)
			null
		}
	}
	
	final override fun release(id: I) {
		entities.compute(id, releaseMapper)
	}
	
	private val releaseMapper = BiFunction { id: I, entry: Entry<R>? ->
		if (entry == null) {
			handleError("Entity is not held (id: $id)")
		}
		else {
			val holds = synchronized(entry) { --entry.holds }
			if (holds == 0) {
				scheduleDispose(id, entry)
			}
			else if (holds < 0) {
				handleError("Excess entity releases (id: $id)")
			}
		}
		entry
	}
	
	fun flush(): Boolean {
		entities.keys.toList().forEach {
			entities.computeIfPresent(it, flushMapper)
		}
		return entities.isEmpty()
	}
	
	private val flushMapper = BiFunction { id: I, entry: Entry<R> ->
		try {
			save(id, entry.entity)
		}
		catch (e: Throwable) {
			handleError("Fail to flush entity (id: $id)", e)
		}
		
		if (entry.holds == 0) {
			entry.killer = Cancelable.DUMMY
			dispose(id, entry.entity)
			null
		}
		else entry
	}
	
	private fun dispose(id: I) {
		entities.compute(id, disposeMapper)
	}
	
	private val disposeMapper = BiFunction { id: I, entry: Entry<R>? ->
		if (entry == null) null
		else flushMapper.apply(id, entry)
	}
	
	private fun scheduleDispose(id: I, entry: Entry<R>) {
		entry.killer.cancel()
		entry.killer = assistant.schedule(lifetime, lifetimeUnit) { dispose(id) }
	}
	
	private class Entry<E : Any>(val entity: E) {
		@Volatile var holds = 0
		@Volatile var killer = Cancelable.DUMMY
	}
}