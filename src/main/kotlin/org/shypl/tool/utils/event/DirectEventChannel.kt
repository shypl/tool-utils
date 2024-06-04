package org.shypl.tool.utils.event

import org.shypl.tool.lang.EMPTY_FUNCTION_1
import org.shypl.tool.utils.Cancelable
import java.util.*
import kotlin.reflect.KClass

class DirectEventChannel<E : Any>(
	type: KClass<E>,
) : ClearableEventChannel<E> {
	
	private val emerger = EventTypeEmerger(type)
	private val receiversMap = HashMap<KClass<out E>, Receivers>()
	
	override fun <T : E> receiveEvent(type: KClass<T>, receiver: (T) -> Unit): Cancelable {
		return receiversMap.getOrPut(type, this::Receivers).add(receiver)
	}
	
	override fun dispatchEvent(event: E) {
		emerger.emerge(event::class).forEach {
			receiversMap[it]?.dispatch(event)
		}
	}
	
	override fun clear() {
		receiversMap.values.forEach { it.clear() }
	}
	
	private inner class Receivers {
		private val receivers = ArrayList<Receiver>()
		
		fun <T : Any> add(receiver: (T) -> Unit): Cancelable {
			@Suppress("UNCHECKED_CAST")
			val r = Receiver(receiver as (Any) -> Unit, receivers)
			receivers.add(r)
			return r
		}
		
		fun dispatch(event: Any) {
			receivers.forEach { it.receive(event) }
		}
		
		fun clear() {
			receivers.toTypedArray().forEach { it.cancel() }
		}
	}
	
	private class Receiver(
		@Volatile private var receiver: (Any) -> Unit,
		@Volatile private var receivers: MutableCollection<Receiver>,
	) : Cancelable {
		override fun cancel() {
			if (receivers.remove(this)) {
				receiver = EMPTY_FUNCTION_1
				receivers = Collections.emptyList()
			}
		}
		
		fun receive(event: Any) {
			receiver.invoke(event)
		}
	}
}