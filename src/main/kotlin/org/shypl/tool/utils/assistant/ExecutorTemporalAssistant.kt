package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.asCancelable
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

open class ExecutorTemporalAssistant(
	executor: ScheduledExecutorService,
	errorHandler: ErrorHandler,
) : AbstractExecutorAssistant<ScheduledExecutorService>(executor, errorHandler), TemporalAssistant {
	
	override fun schedule(delayMillis: Int, code: () -> Unit): Cancelable {
		return schedule(delayMillis.toLong(), TimeUnit.MILLISECONDS, code)
	}
	
	override fun repeat(delayMillis: Int, code: () -> Unit): Cancelable {
		return repeat(delayMillis.toLong(), TimeUnit.MILLISECONDS, code)
	}
	
	override fun schedule(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		require(delay >= 0)
		
		if (executor.isShutdown) return Cancelable.DUMMY
		
		return executor.schedule(createCodeSafetyRunner(code), delay, unit).asCancelable()
	}
	
	override fun repeat(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable {
		require(delay >= 0)
		
		if (executor.isShutdown) return Cancelable.DUMMY
		
		return executor.scheduleAtFixedRate(createCodeSafetyRunner(code), delay, delay, unit).asCancelable()
	}
}