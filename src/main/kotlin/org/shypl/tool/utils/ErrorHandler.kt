package org.shypl.tool.utils

fun interface ErrorHandler {
	fun handleError(message: String, error: Throwable?)
	
	fun handleErrorFullSafety(message: String, error: Throwable? = null) {
		try {
			handleError(message, error)
		}
		catch (e: Throwable) {
			System.err.println(
				"Error on handle error after '$message'\n"
					+ (error?.let { "> Source cause:\n" + it.stackTraceToString().prependIndent(" ") + "\n" } ?: "")
					+ "> Handle cause:\n" + e.stackTraceToString().prependIndent(" ") + "\n"
			)
		}
	}
}

inline fun ErrorHandler.protect(message: String, code: () -> Unit) {
	try {
		code()
	}
	catch (e: Throwable) {
		handleErrorFullSafety(message, e)
	}
}

