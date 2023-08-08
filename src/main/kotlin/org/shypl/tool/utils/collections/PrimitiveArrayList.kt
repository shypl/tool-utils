package org.shypl.tool.utils.collections

import org.shypl.tool.lang.checkIndex


interface PrimitiveArrayList<E> : List<E> {
	operator fun set(index: Int, element: E): E
	
	fun <T> getSourceArray(): T
}

abstract class AbstractPrimitiveArrayList<E, A>(protected val array: A) : AbstractList<E>(), PrimitiveArrayList<E>, RandomAccess {
	override fun get(index: Int): E {
		checkIndex(index)
		return get0(index)
	}
	
	override fun set(index: Int, element: E): E {
		checkIndex(index)
		val old = get0(index)
		set0(index, element)
		return old
	}
	
	protected abstract fun get0(index: Int): E
	
	protected abstract fun set0(index: Int, element: E)
	
	override fun <T> getSourceArray(): T {
		@Suppress("UNCHECKED_CAST")
		return array as T
	}
}

open class BooleanArrayList(array: BooleanArray) : AbstractPrimitiveArrayList<Boolean, BooleanArray>(array) {
	constructor(size: Int) : this(BooleanArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Boolean): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Boolean): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Boolean): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Boolean> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Boolean {
		return array[index]
	}
	
	override fun set0(index: Int, element: Boolean) {
		array[index] = element
	}
}

open class ByteArrayList(array: ByteArray) : AbstractPrimitiveArrayList<Byte, ByteArray>(array) {
	constructor(size: Int) : this(ByteArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Byte): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Byte): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Byte): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Byte> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Byte {
		return array[index]
	}
	
	override fun set0(index: Int, element: Byte) {
		array[index] = element
	}
}

open class DoubleArrayList(array: DoubleArray) : AbstractPrimitiveArrayList<Double, DoubleArray>(array) {
	constructor(size: Int) : this(DoubleArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Double): Boolean {
		return array.any { it == element }
	}
	
	override fun indexOf(element: Double): Int {
		return array.indexOfFirst { it == element }
	}
	
	override fun lastIndexOf(element: Double): Int {
		return array.indexOfLast { it == element }
	}
	
	override fun iterator(): Iterator<Double> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Double {
		return array[index]
	}
	
	override fun set0(index: Int, element: Double) {
		array[index] = element
	}
}

open class IntArrayList(array: IntArray) : AbstractPrimitiveArrayList<Int, IntArray>(array) {
	constructor(size: Int) : this(IntArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Int): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Int): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Int): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Int> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Int {
		return array[index]
	}
	
	override fun set0(index: Int, element: Int) {
		array[index] = element
	}
}

open class LongArrayList(array: LongArray) : AbstractPrimitiveArrayList<Long, LongArray>(array) {
	constructor(size: Int) : this(LongArray(size))
	
	override val size: Int
		get() = array.size
	
	override fun contains(element: Long): Boolean {
		return array.contains(element)
	}
	
	override fun indexOf(element: Long): Int {
		return array.indexOf(element)
	}
	
	override fun lastIndexOf(element: Long): Int {
		return array.lastIndexOf(element)
	}
	
	override fun iterator(): Iterator<Long> {
		return array.iterator()
	}
	
	override fun get0(index: Int): Long {
		return array[index]
	}
	
	override fun set0(index: Int, element: Long) {
		array[index] = element
	}
}