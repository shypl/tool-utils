package org.shypl.tool.utils.collections

interface Queue<E> : List<E> {
	fun add(element: E): Boolean
	
	fun poll(): E?
	
	fun clear()
}