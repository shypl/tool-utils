package org.shypl.tool.utils.event

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.superclasses

interface EventEmerger<E : Any> {
	fun emerge(type: KClass<*>): Array<KClass<out E>>
	
	class Shallow<E : Any> : EventEmerger<E> {
		private val map = ConcurrentHashMap<KClass<*>, Array<KClass<out E>>>()
		
		override fun emerge(type: KClass<*>): Array<KClass<out E>> {
			return map.getOrPut(type) {
				@Suppress("UNCHECKED_CAST")
				arrayOf(type as KClass<out E>)
			}
		}
	}
	
	class Deep<E : Any>(private val type: KClass<E>) : EventEmerger<E> {
		
		private val map = ConcurrentHashMap<KClass<*>, Array<KClass<out E>>>()
		
		override fun emerge(type: KClass<*>): Array<KClass<out E>> {
			return map.getOrPut(type) {
				val list = mutableListOf<KClass<out E>>()
				collectSuperClasses(type, list)
				convertTypes(list)
			}
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
}
