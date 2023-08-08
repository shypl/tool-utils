package org.shypl.tool.utils.pool

interface ObjectPool<T : Any> {
	fun take(): T
	
	fun back(instance: T)
	
	fun clear()
}

inline fun <T : Any, R> ObjectPool<T>.use(code: (T) -> R): R {
	val instance = take()
	try {
		return code(instance)
	} finally {
		back(instance)
	}
}