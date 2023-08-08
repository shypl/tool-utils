package org.shypl.tool.utils.event

import org.shypl.tool.utils.Cancelable
import kotlin.reflect.KClass

interface EventDealer<E : Any> {
	fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable
}

inline fun <reified T : Any> EventDealer<in T>.receiveEvent(noinline receiver: (T) -> Unit): Cancelable {
	return receiveEvent(T::class, receiver)
}