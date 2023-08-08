package org.shypl.tool.utils.event

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

open class EventTypeEmerger<E : Any>(private val type: KClass<E>) {
	private val map = ConcurrentHashMap<KClass<*>, Array<KClass<out E>>>()
	
	fun emerge(type: KClass<*>): Array<KClass<out E>> {
		return map.getOrPut(type) {
			val list = mutableListOf<KClass<out E>>()
			collectSuperClasses(type, list)
			convertTypes(list)
		}
	}
	
	fun match(type: KClass<*>): Boolean {
		return emerge(type).isNotEmpty()
	}
	
	private fun collectSuperClasses(type: KClass<*>, target: MutableList<KClass<out E>>) {
		if (type.isSubclassOf(this.type)) {
			@Suppress("UNCHECKED_CAST")
			target.add(type as KClass<out E>)
			type.superclasses.forEach { collectSuperClasses(it, target) }
		}
	}
	
	private fun convertTypes(list: MutableList<KClass<out E>>): Array<KClass<out E>> {
		@Suppress("UNCHECKED_CAST")
		return if (list.isEmpty()) (EMPTY as Array<KClass<out E>>) else list.toTypedArray()
	}
	
	private companion object {
		private val EMPTY = emptyArray<KClass<*>>()
	}
}