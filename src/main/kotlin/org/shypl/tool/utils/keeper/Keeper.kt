package org.shypl.tool.utils.keeper

interface Keeper<I : Any, E : Any, M : E> {
	fun hold(id: I): M
	
	fun release(id: I)
	
	fun get(id: I): E
	
	fun touch(id: I): E?
}

inline fun <I : Any, E : Any, M : E, R> Keeper<I, E, M>.capture(id: I, block: (M) -> R): R {
	val entity = hold(id)
	try {
		return block(entity)
	}
	finally {
		release(id)
	}
}
