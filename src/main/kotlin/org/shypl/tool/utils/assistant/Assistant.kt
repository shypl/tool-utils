package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.Cancelable

interface Assistant {
	fun execute(code: () -> Unit)
	
	fun charge(code: () -> Unit): Cancelable
}

