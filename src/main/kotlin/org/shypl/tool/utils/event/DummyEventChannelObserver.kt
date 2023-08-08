package org.shypl.tool.utils.event

object DummyEventChannelObserver : EventChannelObserver {
	override fun observeReceiverAdded() {}
	
	override fun observeReceiverRemoved() {}
}