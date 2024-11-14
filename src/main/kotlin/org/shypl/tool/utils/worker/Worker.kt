package org.shypl.tool.utils.worker

import org.shypl.tool.lang.alsoOnFalse
import org.shypl.tool.lang.alsoOnTrue
import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.assistant.Assistant
import org.shypl.tool.utils.collections.ArrayQueue
import kotlin.concurrent.Volatile
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

open class Worker(
	private val assistant: Assistant,
	private val errorHandler: ErrorHandler,
) {
	private val lock = Any()
	private val queue = ArrayQueue<() -> Unit>()
	private val nextTaskFn = ::nextTask
	
	@Volatile private var workingThread: Long = -1
	@Volatile private var _working = false
	@Volatile private var _relaxed = true
	
	val working: Boolean
		get() = _working
	
	val accessible: Boolean
		get() = workingThread == defineCurrentThread()
	
	val relaxed: Boolean
		get() = _relaxed
	
	fun execute(task: () -> Unit) {
		synchronized(lock) {
			if (_working) {
				_relaxed = false
				queue.add(task)
				return
			}
			_working = true
		}
		work(task)
	}
	
	fun defer(task: () -> Unit) {
		synchronized(lock) {
			_relaxed = false
			queue.add(task)
			if (_working) {
				return
			}
			_working = true
		}
		scheduleNextTask()
	}
	
	fun capture(): Boolean {
		synchronized(lock) {
			if (_working) {
				return accessible
			}
			_working = true
		}
		assignWorkingThread()
		return true
	}
	
	fun release() {
		synchronized(lock) {
			if (!_working || !accessible) {
				return
			}
		}
		loseWorkingThread()
		scheduleNextTask()
	}
	
	inline fun runTask(task: () -> Unit) {
		try {
			task()
		}
		catch (e: Throwable) {
			catchRunTaskError(e)
		}
	}
	
	fun catchRunTaskError(error: Throwable) {
		errorHandler.handleErrorFullSafety("Worker run task uncaught error", error)
	}
	
	inline fun withCapture(action: () -> Unit): Boolean {
		if (capture()) {
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
	
	inline fun access(action: () -> Unit): Boolean {
		return accessible.alsoOnTrue {
			runTask(action)
		}
	}
	
	inline fun accessOrExecute(crossinline action: () -> Unit) {
		access(action).alsoOnFalse {
			execute { action() }
		}
	}
	
	inline fun accessOrDefer(crossinline action: () -> Unit) {
		access(action).alsoOnFalse {
			defer { action() }
		}
	}
	
	suspend inline fun <R> suspendExecute(crossinline task: () -> R): R {
		return suspendCoroutine { continuation ->
			execute {
				try {
					continuation.resume(task())
				}
				catch (e: Throwable) {
					continuation.resumeWithException(e)
				}
			}
		}
	}
	
	
	private fun work(task: () -> Unit) {
		assignWorkingThread()
		runTask(task)
		loseWorkingThread()
		scheduleNextTask()
	}
	
	private fun scheduleNextTask() {
		assistant.execute(nextTaskFn)
	}
	
	private fun nextTask() {
		val task: () -> Unit
		synchronized(lock) {
			queue.poll().also {
				if (it == null) {
					_working = false
					return
				}
				task = it
			}
			_relaxed = queue.isEmpty()
		}
		work(task)
	}
	
	private fun defineCurrentThread(): Long {
		return Thread.currentThread().threadId()
	}
	
	private fun assignWorkingThread() {
		workingThread = defineCurrentThread()
	}
	
	private fun loseWorkingThread() {
		workingThread = -1
	}
}