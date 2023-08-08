package org.shypl.tool.utils.event

interface EventChannel<E : Any> : EventDispatcher<E>, EventDealer<E>