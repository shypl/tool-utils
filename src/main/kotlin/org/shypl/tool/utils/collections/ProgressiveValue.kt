package org.shypl.tool.utils.collections

interface ProgressiveValue<P: Comparable<P>, V : Any> {
	val elements: List<Pair<P, V>>
	
	operator fun get(progress: P): V
	
	companion object {
		fun <K: Comparable<K>, V : Any> fromMap(map: Map<K, V>): ProgressiveValue<K, V> {
			return MapProgressiveValue(map)
		}
	}
}

class MapProgressiveValue<P: Comparable<P>, V : Any>(map: Map<P, V>) : ProgressiveValue<P, V> {
	
	override val elements = map.toList().sortedBy { it.first }
	
	override fun get(progress: P): V {
		var last = elements.first().second
		
		for (p in elements) {
			if (p.first == progress) return p.second
			if (p.first > progress) return last
			last = p.second
		}
		
		return last
	}
}