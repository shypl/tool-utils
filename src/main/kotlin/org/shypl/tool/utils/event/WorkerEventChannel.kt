package org.shypl.tool.utils.event

import org.shypl.tool.lang.EMPTY_FUNCTION_1
import org.shypl.tool.utils.Cancelable
import org.shypl.tool.utils.ErrorHandler
import org.shypl.tool.utils.assistant.Assistant
import org.shypl.tool.utils.worker.Worker
import org.shypl.tool.utils.protect
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.KClass

class WorkerEventChannel<E : Any>(
	assistant: Assistant,
	private val errorHandler: ErrorHandler,
	private val emerger: EventTypeEmerger<E>,
	private val observer: EventChannelObserver
) : ClearableEventChannel<E> {
	
	private val worker = Worker(assistant, errorHandler)
	private val receiversMap = ConcurrentHashMap<KClass<out E>, Receivers>()
	
	constructor(type: KClass<E>, assistant: Assistant, errorHandler: ErrorHandler): this(
		assistant,
		errorHandler,
		EventTypeEmerger(type),
		DummyEventChannelObserver
	)
	
	override fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable {
		return receiversMap.getOrPut(type, this::Receivers).add(receiver)
	}
	
	override fun dispatchEvent(event: E) {
		worker.defer {
			emerger.emerge(event::class).forEach {
				receiversMap[it]?.dispatch(event)
			}
		}
	}
	
	override fun clear() {
		worker.defer {
			receiversMap.values.forEach { it.clear() }
		}
	}
	
	private inner class Receivers {
		private val receivers = CopyOnWriteArrayList<Receiver>()
		
		fun <T : Any> add(receiver: (T) -> Unit): Cancelable {
			@Suppress("UNCHECKED_CAST")
			val r = Receiver(receiver as (Any) -> Unit, receivers, observer)
			receivers.add(r)
			observer.observeReceiverAdded()
			return r
		}
		
		fun dispatch(event: Any) {
			receivers.forEach {
				errorHandler.protect("WorkerEventChannel run code uncaught error") {
					it.receive(event)
				}
			}
		}
		
		fun clear() {
			receivers.toList().forEach { it.cancel() }
		}
	}
	
	private class Receiver(
		@Volatile private var receiver: (Any) -> Unit,
		@Volatile private var receivers: MutableCollection<Receiver>,
		@Volatile private var observer: EventChannelObserver
	) : Cancelable {
		override fun cancel() {
			if (receivers.remove(this)) {
				observer.observeReceiverRemoved()
				receiver = EMPTY_FUNCTION_1
				receivers = Collections.emptySet()
				observer = DummyEventChannelObserver
			}
		}
		
		fun receive(event: Any) {
			receiver.invoke(event)
		}
	}
}