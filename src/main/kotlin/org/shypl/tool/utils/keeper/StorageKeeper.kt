package org.shypl.tool.utils.keeper

import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.assistant.TemporalAssistant
import java.util.concurrent.TimeUnit

class StorageKeeper<I : Any, E : Any, M : E, R : M>(
	assistant: TemporalAssistant,
	lifetime: Long,
	lifetimeUnit: TimeUnit,
	private val errorHandler: ErrorHandler,
	private val storage: Storage<I, R>,
) : AbstractKeeper<I, E, M, R>(assistant, lifetime, lifetimeUnit) {
	
	constructor(
		assistant: TemporalAssistant,
		lifetime: Long,
		lifetimeUnit: TimeUnit,
		errorHandler: ErrorHandler,
		load: (I) -> R,
		save: (I, R) -> Unit,
		dispose: (I, R) -> Unit,
	) : this(assistant, lifetime, lifetimeUnit, errorHandler, object : Storage<I, R> {
		override fun loadEntity(id: I) = load(id)
		override fun saveEntity(id: I, entity: R) = save(id, entity)
		override fun disposeEntity(id: I, entity: R) = dispose(id, entity)
	})
	
	override fun load(id: I) = storage.loadEntity(id)
	
	override fun save(id: I, entity: R) = storage.saveEntity(id, entity)
	
	override fun dispose(id: I, entity: R) = storage.disposeEntity(id, entity)
	
	override fun handleError(message: String, error: Throwable?) {
		errorHandler.handleErrorFullSafety(message, error)
	}
}

