package org.shypl.tool.utils.pool

interface ObjectAllocator<T : Any> {
	fun produceInstance(): T
	
	fun clearInstance(instance: T)
	
	fun disposeInstance(instance: T)
}