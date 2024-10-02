package org.shypl.tool.utils.event

import org.shypl.tool.lang.EMPTY_FUNCTION_1
import org.shypl.tool.lang.clearAndEach
import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.protect
import java.util.*
import kotlin.reflect.KClass

class DirectEventChannel<E : Any>(
	private val emerger: EventEmerger<E>,
	private val errorHandler: ErrorHandler
) : ClearableEventChannel<E> {
	
	private val receiversMap = HashMap<KClass<out E>, Receivers>()
	private var deferredQueue: ArrayList<Deferred>? = null
	@Volatile private var dispatching = false
	
	override fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable {
		return receiversMap.getOrPut(type, this::Receivers).add(receiver)
	}
	
	override fun dispatchEvent(event: E) {
		if (dispatching) {
			defer(Deferred.Dispatch(this, event))
		}
		else {
			dispatching = true
			emerger.emerge(event::class).forEach {
				receiversMap[it]?.dispatch(event)
			}
			dispatching = false
			
			processDeferred()
		}
	}
	
	private fun defer(deferred: Deferred) {
		var queue = deferredQueue
		if (queue == null) {
			queue = ArrayList()
			deferredQueue = queue
		}
		queue.add(deferred)
	}
	
	private fun processDeferred() {
		val queue = deferredQueue
		if (!queue.isNullOrEmpty()) {
			queue.clearAndEach(Deferred::execute)
		}
		
	}
	
	override fun clear() {
		if (dispatching) {
			defer(Deferred.Clear(this))
		}
		else {
			receiversMap.values.forEach { it.clear() }
		}
	}
	
	private sealed interface Deferred {
		
		fun execute()
		
		class Dispatch<E : Any>(private val channel: DirectEventChannel<E>, private val event: E) : Deferred {
			override fun execute() = channel.dispatchEvent(event)
		}
		
		class Cancel(private val receiver: DirectEventChannel<out Any>.Receiver) : Deferred {
			override fun execute() = receiver.cancel()
		}
		
		class Clear(private val channel: DirectEventChannel<*>) : Deferred {
			override fun execute() = channel.clear()
		}
	}
	
	private inner class Receivers {
		private val receivers = ArrayList<Receiver>()
		
		fun <T : Any> add(receiver: (T) -> Unit): Cancelable {
			@Suppress("UNCHECKED_CAST")
			val r = Receiver(receiver as (Any) -> Unit, receivers)
			receivers.add(r)
			return r
		}
		
		fun dispatch(event: Any) {
			receivers.forEach {
				errorHandler.protect("DirectEventChannel run code uncaught error") {
					it.receive(event)
				}
			}
		}
		
		fun clear() {
			receivers.clearAndEach { it.cancel() }
		}
	}
	
	private inner class Receiver(
		@Volatile private var receiver: (Any) -> Unit,
		@Volatile private var receivers: MutableCollection<Receiver>,
	) : Cancelable {
		override fun cancel() {
			if (dispatching) {
				defer(Deferred.Cancel(this))
			}
			else {
				receivers.remove(this)
				receivers = Collections.emptyList()
				receiver = EMPTY_FUNCTION_1
			}
		}
		
		fun receive(event: Any) {
			receiver(event)
		}
	}
}