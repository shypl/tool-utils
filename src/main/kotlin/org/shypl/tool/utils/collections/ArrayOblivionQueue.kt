package org.shypl.tool.utils.collections

import org.shypl.tool.lang.checkIndex

class ArrayOblivionQueue<E>(
	private val capacity: Int
) : Queue<E>, AbstractList<E>() {
	private val array = arrayOfNulls<Any>(capacity)
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
		if (capacity == 0) return false
		
		array[tail] = element
		tail = (tail + 1) % capacity
		
		if (_size == capacity) {
			head = (head + 1) % capacity
		}
		else {
			++_size
		}
		
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
}