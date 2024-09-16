package org.shypl.tool.utils.collections

import org.shypl.tool.lang.alsoOnTrue
import java.util.function.BiFunction
import java.util.function.Function

interface BiMap<K, V> : Map<K, V> {
	fun getByValue(value: V): K?
}

interface MutableBiMap<K, V> : BiMap<K, V>, MutableMap<K, V>

fun <K, V> Map<K, V>.toBiMap(): BiMap<K, V> {
	return toMutableBiMap()
}

fun <K, V> Map<K, V>.toMutableBiMap(): MutableBiMap<K, V> {
	return HashBiMap<K, V>(this.size).also {
		it.putAll(this)
	}
}

fun <K, V> biMapOf(vararg pairs: Pair<K, V>) : BiMap<K, V> {
	return mutableBiMapOf(*pairs)
}

fun <K, V> mutableBiMapOf(vararg pairs: Pair<K, V>) : MutableBiMap<K, V> {
	return HashBiMap<K, V>(pairs.size).also {
		it.putAll(pairs)
	}
}


class HashBiMap<K, V>(initialCapacity: Int) : HashMap<K, V>(initialCapacity), MutableBiMap<K, V> {
	private val reverse = HashMap<V, K>()
	
	override fun getByValue(value: V): K? {
		return reverse[value]
	}
	
	override fun clear() {
		super.clear()
		reverse.clear()
	}
	
	override fun put(key: K, value: V): V? {
		reverse[value] = key
		return super.put(key, value)
	}
	
	override fun putAll(from: Map<out K, V>) {
		reverse.putAll(from.map { it.value to it.key }.toMap())
		super.putAll(from)
	}
	
	override fun remove(key: K): V? {
		return super<HashMap>.remove(key).also {
			reverse.remove(it)
		}
	}
	
	override fun remove(key: K, value: V): Boolean {
		return super<HashMap>.remove(key, value).alsoOnTrue {
			reverse.remove(value)
		}
	}
	
	override fun replaceAll(function: BiFunction<in K, in V, out V>) {
		throw UnsupportedOperationException()
	}
	
	override fun putIfAbsent(key: K, value: V): V? {
		throw UnsupportedOperationException()
	}
	
	override fun replace(key: K, oldValue: V, newValue: V): Boolean {
		throw UnsupportedOperationException()
	}
	
	override fun replace(key: K, value: V): V? {
		throw UnsupportedOperationException()
	}
	
	override fun computeIfAbsent(key: K, mappingFunction: Function<in K, out V>): V {
		throw UnsupportedOperationException()
	}
	
	override fun computeIfPresent(key: K, remappingFunction: BiFunction<in K, in V & Any, out V?>): V? {
		throw UnsupportedOperationException()
	}
	
	override fun compute(key: K, remappingFunction: BiFunction<in K, in V?, out V?>): V? {
		throw UnsupportedOperationException()
	}
	
	override fun merge(key: K, value: V & Any, remappingFunction: BiFunction<in V & Any, in V & Any, out V?>): V? {
		throw UnsupportedOperationException()
	}
	
	override fun clone(): Any {
		return toMutableBiMap()
	}
	
	
}