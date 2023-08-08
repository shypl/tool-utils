package org.shypl.tool.utils.collections

interface KeyedSet<K : Any, out E : Any> : Set<E> {
	val keys: Set<K>
	
	operator fun get(key: K): E?
	
	fun containsKey(key: K): Boolean
}

interface MutableKeyedSet<K : Any, E : Any> : MutableCollection<E>, KeyedSet<K, E> {
	fun removeKey(key: K): E?
}


inline fun <K : Any, E : Any> MutableKeyedSet<K, E>.getOrAdd(key: K, element: (K) -> E): E {
	var e = get(key)
	if (e == null) {
		e = element(key)
		add(e)
	}
	return e
}

fun <K : Any, E : Any> Collection<E>.toKeySet(keyer: (E) -> K): KeyedSet<K, E> {
	return MapKeyedSet(keyer, HashMap<K, E>(size).addElements(keyer, this))
}

fun <K : Any, E : Any> Collection<E>.toMutableKeySet(keyer: (E) -> K): MutableKeyedSet<K, E> {
	return MapMutableKeyedSet(keyer, HashMap<K, E>(size).addElements(keyer, this))
}

fun <K : Any, E : Any> Iterable<E>.toKeySet(keyer: (E) -> K): KeyedSet<K, E> {
	return MapKeyedSet(keyer, HashMap<K, E>().addElements(keyer, this))
}

fun <K : Any, E : Any> Iterable<E>.toMutableKeySet(keyer: (E) -> K): MutableKeyedSet<K, E> {
	return MapMutableKeyedSet(keyer, HashMap<K, E>().addElements(keyer, this))
}

fun <K : Any, E : Any> Array<E>.toKeySet(keyer: (E) -> K): KeyedSet<K, E> {
	return MapKeyedSet(keyer, HashMap<K, E>(size).addElements(keyer, this))
}

fun <K : Any, E : Any> Array<E>.toMutableKeySet(keyer: (E) -> K): MutableKeyedSet<K, E> {
	return MapMutableKeyedSet(keyer, HashMap<K, E>(size).addElements(keyer, this))
}



fun <K : Any, E : Any> emptyKeyedSet(): KeyedSet<K, E> {
	@Suppress("UNCHECKED_CAST")
	return EmptyKeyedSet as KeyedSet<K, E>
}

fun <K : Any, E : Any> keyedSetOf(@Suppress("UNUSED_PARAMETER") keyer: (E) -> K): KeyedSet<K, E> {
	return emptyKeyedSet()
}

fun <K : Any, E : Any> keyedSetOf(keyer: (E) -> K, vararg elements: E): KeyedSet<K, E> {
	return MapKeyedSet(keyer, elementsToHashMap(keyer, elements))
}

fun <K : Any, E : Any> mutableKeyedSetOf(keyer: (E) -> K): MutableKeyedSet<K, E> {
	return MapMutableKeyedSet(keyer, HashMap())
}

fun <K : Any, E : Any> mutableKeyedSetOf(keyer: (E) -> K, vararg elements: E): MutableKeyedSet<K, E> {
	return MapMutableKeyedSet(keyer, elementsToHashMap(keyer, elements))
}

private fun <K : Any, E : Any> elementsToHashMap(keyer: (E) -> K, elements: Array<out E>): HashMap<K, E> {
	return HashMap<K, E>(elements.size).also { m -> elements.forEach { m[keyer(it)] = it } }
}

private fun <K : Any, E : Any> MutableMap<K, E>.addElements(keyer: (E) -> K, elements: Iterable<E>): MutableMap<K, E> {
	elements.forEach { set(keyer(it), it) }
	return this
}



fun <K : Any, E : Any, R: Any> Collection<E>.mapKeySet(keyer: (R) -> K, mapper: (E) -> R): KeyedSet<K, R> {
	return MapKeyedSet(keyer, HashMap<K, R>(size).addElements(keyer, map(mapper)))
}

fun <K : Any, E : Any, R: Any> Collection<E>.mapMutableKeySet(keyer: (R) -> K, mapper: (E) -> R): MutableKeyedSet<K, R> {
	return MapMutableKeyedSet(keyer, HashMap<K, R>(size).addElements(keyer, map(mapper)))
}


fun <K : Any, E : Any, R: Any> Iterable<E>.mapKeySet(keyer: (R) -> K, mapper: (E) -> R): KeyedSet<K, R> {
	return MapKeyedSet(keyer, HashMap<K, R>().addElements(keyer, map(mapper)))
}

fun <K : Any, E : Any, R: Any> Iterable<E>.mapMutableKeySet(keyer: (R) -> K, mapper: (E) -> R): MutableKeyedSet<K, R> {
	return MapMutableKeyedSet(keyer, HashMap<K, R>().addElements(keyer, map(mapper)))
}


fun <K : Any, E : Any, R: Any> Array<E>.mapKeySet(keyer: (R) -> K, mapper: (E) -> R): KeyedSet<K, R> {
	return MapKeyedSet(keyer, HashMap<K, R>(size).addElements(keyer, map(mapper)))
}

fun <K : Any, E : Any, R: Any> Array<E>.mapMutableKeySet(keyer: (R) -> K, mapper: (E) -> R): MutableKeyedSet<K, R> {
	return MapMutableKeyedSet(keyer, HashMap<K, R>(size).addElements(keyer, map(mapper)))
}


private fun <K : Any, E : Any> MutableMap<K, E>.addElements(keyer: (E) -> K, elements: Array<E>): MutableMap<K, E> {
	elements.forEach { set(keyer(it), it) }
	return this
}

private object EmptyKeyedSet : KeyedSet<Nothing, Nothing> {
	override val size: Int get() = 0
	override val keys: Set<Nothing> get() = emptySet()
	override fun isEmpty(): Boolean = true
	override fun get(key: Nothing): Nothing? = null
	override fun containsKey(key: Nothing): Boolean = false
	override fun contains(element: Nothing): Boolean = false
	override fun containsAll(elements: Collection<Nothing>): Boolean = false
	override fun iterator(): Iterator<Nothing> = emptyList<Nothing>().iterator()
}


abstract class AbstractKeyedSet<K : Any, E : Any>(protected val keyer: (E) -> K) : KeyedSet<K, E> {
	override fun contains(element: E): Boolean {
		return containsKey(keyer(element))
	}
}

abstract class AbstractMapKeyedSet<K : Any, E : Any, M : Map<K, E>>(keyer: (E) -> K, protected val map: M) : AbstractKeyedSet<K, E>(keyer) {
	override val size: Int
		get() = map.size
	
	override val keys: Set<K>
		get() = map.keys
	
	override fun containsKey(key: K): Boolean {
		return map.containsKey(key)
	}
	
	override fun containsAll(elements: Collection<E>): Boolean {
		return map.values.containsAll(elements)
	}
	
	override fun isEmpty(): Boolean {
		return map.isEmpty()
	}
	
	override fun iterator(): Iterator<E> {
		return map.values.iterator()
	}
	
	override fun get(key: K): E? {
		return map[key]
	}
}


open class MapKeyedSet<K : Any, E : Any>(keyer: (E) -> K, map: Map<K, E>) : AbstractMapKeyedSet<K, E, Map<K, E>>(keyer, map)

open class MapMutableKeyedSet<K : Any, E : Any>(keyer: (E) -> K, map: MutableMap<K, E>) : AbstractMapKeyedSet<K, E, MutableMap<K, E>>(keyer, map), MutableKeyedSet<K, E> {
	override val size: Int
		get() = map.size
	
	override fun containsKey(key: K): Boolean {
		return map.containsKey(key)
	}
	
	override fun removeKey(key: K): E? {
		return map.remove(key)
	}
	
	override fun containsAll(elements: Collection<E>): Boolean {
		return map.values.containsAll(elements)
	}
	
	override fun isEmpty(): Boolean {
		return map.isEmpty()
	}
	
	override fun addAll(elements: Collection<E>): Boolean {
		return elements.fold(false) { m, e -> add(e) || m }
	}
	
	override fun clear() {
		map.clear()
	}
	
	override fun iterator(): MutableIterator<E> {
		return map.values.iterator()
	}
	
	override fun removeAll(elements: Collection<E>): Boolean {
		return elements.fold(false) { m, e -> remove(e) || m }
	}
	
	override fun retainAll(elements: Collection<E>): Boolean {
		var m = false
		val iterator = iterator()
		while (iterator.hasNext()) {
			val e = iterator.next()
			if (!elements.contains(e)) {
				iterator.remove()
				m = true
			}
		}
		return m
	}
	
	override fun add(element: E): Boolean {
		val key = keyer(element)
		val prev = map.put(key, element)
		return prev == null || prev != element
	}
	
	override fun remove(element: E): Boolean {
		val key = keyer(element)
		val prev = map.remove(key)
		return prev != null
	}
	
	override fun get(key: K): E? {
		return map[key]
	}
}
