package org.shypl.tool.utils

interface MaybeStartable {
	fun start(): Stoppable?
	
	companion object {
		val DUMMY = object : MaybeStartable {
			override fun start(): Stoppable? = null
		}
	}
}

interface Startable : MaybeStartable {
	override fun start(): Stoppable
	
	companion object {
		
		val DUMMY = object : Startable {
			override fun start(): Stoppable = Stoppable.DUMMY
		}
		
		inline operator fun invoke(crossinline block: () -> Stoppable): Startable {
			return object : Startable {
				override fun start(): Stoppable = block()
			}
		}
	}

}
