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
	
	override fun load(id: I) = storage.loadEntity(id)
	
	override fun save(id: I, entity: R) = storage.saveEntity(id, entity)
	
	override fun dispose(id: I, entity: R) = storage.disposeEntity(id, entity)
	
	override fun handleError(message: String, error: Throwable?) {
		errorHandler.handleErrorFullSafety(message, error)
	}
}