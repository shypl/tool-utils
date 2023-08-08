package org.shypl.tool.utils.pool

abstract class AbstractObjectPool<T : Any>(private val allocator: ObjectAllocator<T>) : ObjectPool<T> {
	protected fun produceInstance(): T {
		return allocator.produceInstance()
	}
	
	protected fun clearInstance(instance: T) {
		allocator.clearInstance(instance)
	}
	
	protected fun disposeInstance(instance: T) {
		allocator.disposeInstance(instance)
	}
}


