package org.shypl.tool.utils.event

interface EventDispatcher<E : Any> {
	fun dispatchEvent(event: E)
}