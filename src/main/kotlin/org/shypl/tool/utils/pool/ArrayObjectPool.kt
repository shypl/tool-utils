package org.shypl.tool.utils.pool

import java.util.concurrent.ArrayBlockingQueue

class ArrayObjectPool<T : Any>(
	capacity: Int,
	allocator: ObjectAllocator<T>
) : ObjectPool<T>, AbstractObjectPool<T>(allocator) {
	
	private val instances = ArrayBlockingQueue<T>(capacity)
	
	override fun take(): T {
		return instances.poll() ?: return produceInstance()
	}
	
	override fun back(instance: T) {
		clearInstance(instance)
		if(!instances.offer(instance)) {
			disposeInstance(instance)
		}
	}
	
	override fun clear() {
		while (true) {
			val instance = instances.poll() ?: return
			disposeInstance(instance)
		}
	}
}