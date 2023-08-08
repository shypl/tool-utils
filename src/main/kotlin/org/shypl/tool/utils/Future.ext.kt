package org.shypl.tool.utils

import java.util.concurrent.Future

fun Future<*>.asCancelable() = Cancelable {
	cancel(false)
}