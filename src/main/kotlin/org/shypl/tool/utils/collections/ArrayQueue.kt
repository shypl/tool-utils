package org.shypl.tool.utils.collections

import org.shypl.tool.lang.checkCollectionIndex
import org.shypl.tool.lang.checkIndex

class ArrayQueue<E>(
	initialCapacity: Int = 10
) : Queue<E>, AbstractList<E>() {
	
	private var capacity = initialCapacity
	private var array = arrayOfNulls<Any>(capacity)
	private var head = 0
	private var tail = 0
	private var _size = 0
	
	override val size: Int
		get() = _size
	
	override fun clear() {
		_size = 0
		head = 0
		tail = 0
		array.fill(null)
	}
	
	override fun add(element: E): Boolean {
		ensureArrayCapacity(_size + 1)
		array[tail] = element
		tail = (tail + 1) % capacity
		++_size
		return true
	}
	
	override fun poll(): E? {
		if (_size == 0) return null
		
		val element = array[head]
		
		array[head] = null
		head = (head + 1) % capacity
		--_size
		
		if (_size == 0) {
			head = 0
			tail = 0
		}
		
		@Suppress("UNCHECKED_CAST")
		return element as E
	}
	
	override fun get(index: Int): E {
		checkIndex(index)
		
		@Suppress("UNCHECKED_CAST")
		return array[(head + index) % capacity] as E
	}
	
	
	private fun ensureArrayCapacity(min: Int) {
		if (min > capacity) {
			var newCapacity = capacity + capacity.shr(1)
			if (newCapacity < min) {
				newCapacity = min
			}
			val newArray = arrayOfNulls<Any>(newCapacity)
			if (head < tail) {
				array.copyInto(newArray, 0, head, tail)
			}
			else {
				array.copyInto(newArray, 0, head)
				array.copyInto(newArray, capacity - head, 0, tail)
			}
			array = newArray
			capacity = newCapacity
			head = 0
			tail = _size
		}
		else if (min < 0) {
			throw RuntimeException("Out of memory")
		}
	}
}