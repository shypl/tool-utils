package org.shypl.tool.utils.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class TestArrayQueue {
	@Test
	fun `queue circle`() {
		val queue = ArrayQueue<Int>(3)
		
		queue.add(1)
		queue.add(2)
		queue.add(3)
		
		assertEquals(1, queue.poll())
		assertEquals(2, queue.poll())
		
		queue.add(4)
		queue.add(5)
		
		assertEquals(3, queue.poll())
		assertEquals(4, queue.poll())
		assertEquals(5, queue.poll())
		assertEquals(null, queue.poll())
		
		queue.add(1)
		queue.add(2)
		queue.add(3)
		queue.add(4)
		queue.add(5)
		
		assertEquals(1, queue.poll())
		assertEquals(2, queue.poll())
		assertEquals(3, queue.poll())
		
		queue.add(6)
		queue.add(7)
		queue.add(8)
		queue.add(9)
		queue.add(10)
		queue.add(11)
		queue.add(12)
		queue.add(13)
		queue.add(14)
		
		assertEquals(4, queue.poll())
		assertEquals(5, queue.poll())
		assertEquals(6, queue.poll())
		assertEquals(7, queue.poll())
		assertEquals(8, queue.poll())
		assertEquals(9, queue.poll())
		assertEquals(10, queue.poll())
		assertEquals(11, queue.poll())
		assertEquals(12, queue.poll())
		assertEquals(13, queue.poll())
		assertEquals(14, queue.poll())
		assertEquals(null, queue.poll())
	}
}