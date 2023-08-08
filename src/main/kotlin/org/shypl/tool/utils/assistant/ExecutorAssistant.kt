package org.shypl.tool.utils.assistant

import org.shypl.tool.utils.ErrorHandler
import java.util.concurrent.ExecutorService

class ExecutorAssistant(
	executor: ExecutorService,
	errorHandler: ErrorHandler,
) : AbstractExecutorAssistant<ExecutorService>(executor, errorHandler)