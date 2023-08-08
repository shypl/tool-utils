package org.shypl.tool.utils.worker

import org.shypl.tool.lang.alsoOnTrue
import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.assistant.Assistant
import kotlin.concurrent.Volatile
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class LivingWorker(
	assistant: Assistant,
	errorHandler: ErrorHandler,
) : Worker(assistant, errorHandler) {
	@Volatile private var _alive = true
	
	val alive
		get() = _alive
	
	fun die() {
		if (_alive) {
			if (accessible) {
				_alive = false
			}
			else {
				execute(::die)
			}
		}
	}
	
	
	inline fun accessOnLive(action: () -> Unit): Boolean {
		return (alive && accessible).alsoOnTrue {
			runTask(action)
		}
	}
	
	inline fun executeOnLive(crossinline task: () -> Unit) {
		if (alive) {
			execute {
				if (alive) {
					task()
				}
			}
		}
	}
	
	inline fun deferOnLive(crossinline task: () -> Unit) {
		if (alive) {
			defer {
				if (alive) {
					task()
				}
			}
		}
	}
	
	fun captureOnLive(): Boolean {
		if (alive && capture()) {
			if (alive) {
				return true
			}
			release()
		}
		return false
	}
	
	inline fun withCaptureOnLive(action: () -> Unit): Boolean {
		if (captureOnLive()) {
			try {
				runTask(action)
			}
			finally {
				release()
			}
			return true
		}
		return false
	}
	
	inline fun accessOrExecuteOnLive(crossinline action: () -> Unit) {
		if (alive) {
			if (accessible) {
				runTask(action)
			}
			else {
				execute {
					if (alive) {
						action()
					}
				}
			}
		}
	}
	
	inline fun accessOrExecuteOnLive(onAccess: () -> Unit, crossinline onExecute: () -> Unit) {
		if (alive) {
			if (accessible) {
				runTask(onAccess)
			}
			else {
				execute {
					if (alive) {
						onExecute()
					}
				}
			}
		}
	}
	
	inline fun accessOrDeferOnLive(crossinline action: () -> Unit) {
		if (alive) {
			if (accessible) {
				runTask(action)
			}
			else {
				defer {
					if (alive) {
						action()
					}
				}
			}
		}
	}
	
	inline fun accessOrDeferOnLive(onAccess: () -> Unit, crossinline onExecute: () -> Unit) {
		if (alive) {
			if (accessible) {
				runTask(onAccess)
			}
			else {
				defer {
					if (alive) {
						onExecute()
					}
				}
			}
		}
	}
	
	suspend inline fun <R> suspendExecuteOnLive(crossinline task: () -> R): R {
		return suspendCoroutine { continuation ->
			if (alive) {
				execute {
					if (alive) {
						try {
							continuation.resume(task())
						}
						catch (e: Throwable) {
							continuation.resumeWithException(e)
						}
					}
					else {
						continuation.resumeWithException(IllegalStateException("Worker is die"))
					}
				}
			}
			else {
				continuation.resumeWithException(IllegalStateException("Worker is die"))
			}
		}
	}
}