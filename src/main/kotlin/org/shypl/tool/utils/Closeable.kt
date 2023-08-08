package org.shypl.tool.utils

import java.io.Closeable

object DummyCloseable : Closeable {
	override fun close() {}
}