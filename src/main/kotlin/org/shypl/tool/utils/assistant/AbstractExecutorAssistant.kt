package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.asCancelable
import java.util.concurrent.ExecutorService

abstract class AbstractExecutorAssistant<E : ExecutorService>(
	protected val executor: E,
	private val errorHandler: ErrorHandler,
) : Assistant {
	
	override fun execute(code: () -> Unit) {
		if (!executor.isShutdown) executor.execute(createCodeSafetyRunner(code))
	}
	
	override fun charge(code: () -> Unit): Cancelable {
		if (executor.isShutdown) return Cancelable.DUMMY
		return executor.submit(createCodeSafetyRunner(code)).asCancelable()
	}
	
	protected fun createCodeSafetyRunner(code: () -> Unit): Runnable {
		return CodeSafetyRunner(code, errorHandler)
	}
}
