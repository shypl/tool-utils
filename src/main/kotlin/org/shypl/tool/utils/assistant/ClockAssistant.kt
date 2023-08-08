package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.Cancelable
import java.time.Clock
import java.time.LocalDateTime

interface ClockAssistant : TemporalAssistant {
	val clock: Clock
	
	fun schedule(point: LocalDateTime, code: () -> Unit): Cancelable
}