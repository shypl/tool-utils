package org.shypl.tool.utils.event

interface EventChannelObserver {
	fun observeReceiverAdded()
	
	fun observeReceiverRemoved()
}