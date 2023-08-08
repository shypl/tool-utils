package org.shypl.tool.utils.event

interface ClearableEventChannel<E : Any> : EventChannel<E> {
	fun clear()
}