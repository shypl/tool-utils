package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.ErrorHandler
import java.time.Clock
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class ExecutorClockAssistant(
	override val clock: Clock,
	executor: ScheduledExecutorService,
	errorHandler: ErrorHandler,
) : ExecutorTemporalAssistant(executor, errorHandler), ClockAssistant {
	override fun schedule(point: LocalDateTime, code: () -> Unit): Cancelable {
		val delay = LocalDateTime.now(clock).until(point, ChronoUnit.NANOS).coerceAtLeast(0)
		return schedule(delay, TimeUnit.NANOSECONDS, code)
	}
}