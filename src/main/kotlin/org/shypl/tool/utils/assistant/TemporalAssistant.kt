package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.Cancelable
import java.util.concurrent.TimeUnit

interface TemporalAssistant : Assistant {
	fun schedule(delayMillis: Int, code: () -> Unit): Cancelable
	
	fun repeat(delayMillis: Int, code: () -> Unit): Cancelable
	
	fun schedule(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable
	
	fun repeat(delay: Long, unit: TimeUnit, code: () -> Unit): Cancelable
}