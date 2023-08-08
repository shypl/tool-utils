package org.shypl.tool.utils

import kotlin.concurrent.Volatile

interface Cancelable {
	fun cancel()
	
	companion object {
		val DUMMY = object : Cancelable {
			override fun cancel() {}
		}
		
		inline operator fun invoke(crossinline block: () -> Unit): Cancelable {
			return object : Cancelable {
				override fun cancel() = block.invoke()
			}
		}
		
		fun composite(vararg targets: Cancelable): Cancelable {
			return CompositeCancelable(targets.toList())
		}
	}
}

class MediatorCancelable(
	@Volatile private var target: Cancelable? = null,
) : Cancelable {
	override fun cancel() {
		target?.cancel()
		this.release()
	}
	
	fun assign(target: Cancelable) {
		this.target = target
	}
	
	fun release() {
		target = null
	}
}

class CompositeCancelable(targets: Collection<Cancelable>) : Cancelable {
	private val targets = targets.toMutableList()
	
	override fun cancel() {
		targets.forEach(Cancelable::cancel)
		targets.clear()
	}
}