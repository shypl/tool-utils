package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.protect

internal class CodeSafetyRunner(private val code: () -> Unit, private val errorHandler: ErrorHandler) : Runnable {
	override fun run() {
		errorHandler.protect("Assistant run code uncaught error", code)
	}
}