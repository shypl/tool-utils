package org.shypl.tool.utils.keeper

interface Storage<I : Any, R : Any> {
	fun loadEntity(id: I): R
	
	fun saveEntity(id: I, entity: R)
	
	fun disposeEntity(id: I, entity: R)
}