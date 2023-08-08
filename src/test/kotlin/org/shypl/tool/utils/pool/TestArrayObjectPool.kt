package org.shypl.tool.utils.pool

import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class TestArrayObjectPool {
	
	@Test
	fun capacity() {
		
		val allocator = object : ObjectAllocator<Int> {
			
			var produces = 0
			var clears = 0
			var disposes = 0
			
			override fun produceInstance(): Int {
				return ++produces
			}
			
			override fun clearInstance(instance: Int) {
				++clears
			}
			
			override fun disposeInstance(instance: Int) {
				++disposes
			}
		}
		
		val pool = ArrayObjectPool(3, allocator)
		
		assertEquals(1, pool.take())
		assertEquals(2, pool.take())
		assertEquals(3, pool.take())
		assertEquals(4, pool.take())
		
		pool.back(1)
		pool.back(2)
		pool.back(3)
		pool.back(4)
		
		assertEquals(1, pool.take())
		assertEquals(2, pool.take())
		assertEquals(3, pool.take())
		assertEquals(5, pool.take())
		
		pool.back(1)
		pool.back(2)
		pool.back(3)
		pool.back(5)
		
		pool.clear()
		
		assertEquals(5, allocator.produces)
		assertEquals(8, allocator.clears)
		assertEquals(5, allocator.disposes)
	}
	
	@Test
	fun concurrent() {
		class Stub {
			val using = AtomicBoolean(false)
		}
		
		val produces = AtomicInteger()
		val clears = AtomicInteger()
		val disposes = AtomicInteger()
		val uses = AtomicInteger()
		
		val allocator = object : ObjectAllocator<Stub> {
			override fun produceInstance(): Stub {
				produces.getAndIncrement()
				return Stub()
			}
			
			override fun clearInstance(instance: Stub) {
				clears.getAndIncrement()
			}
			
			override fun disposeInstance(instance: Stub) {
				disposes.getAndIncrement()
			}
		}
		
		val tasks = 100
		val times = 10
		val pool = ArrayObjectPool(64, allocator)
		val executor = Executors.newFixedThreadPool(10)
		
		repeat(tasks) {
			executor.execute {
				repeat(times) {
					pool.use {
						val actual = it.using.getAndSet(true)
						assertFalse(actual)
						uses.getAndIncrement()
						Thread.sleep(Random.nextLong(1, 10))
						it.using.set(false)
					}
				}
			}
		}
		
		executor.shutdown()
		executor.awaitTermination(10, TimeUnit.SECONDS)
		pool.clear()
		
		
		val expectedUses = tasks * times
		
		assertEquals(produces.get(), disposes.get(), "Creates and disposes")
		assertEquals(expectedUses, uses.get(), "Uses")
		assertEquals(uses.get(), clears.get(), "Clears")
	}
}