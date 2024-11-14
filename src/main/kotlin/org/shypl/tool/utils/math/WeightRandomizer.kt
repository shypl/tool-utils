package org.shypl.tool.utils.math

import java.util.function.Supplier
import kotlin.random.Random

abstract class WeightRandomizer {
	protected abstract val totalWeight: Int
	
	protected abstract fun getWeight(index: Int): Int
	
	fun nextIndex(): Int {
		val r = Random.nextInt(totalWeight)
		var a = 0
		var i = 0
		
		while (true) {
			a += getWeight(i)
			if (a > r) {
				return i
			}
			++i
		}
	}
	
	fun nextWeight(): Int {
		return getWeight(nextIndex())
	}
	
	class Fixed(private var weights: IntArray) : WeightRandomizer() {
		constructor(weights: List<Int>) : this(weights.toIntArray())
		
		override val totalWeight = weights.sum()
		override fun getWeight(index: Int) = weights[index]
	}
	
	class Dynamic(private val weights: List<Supplier<Int>>) : WeightRandomizer() {
		
		override var totalWeight: Int = 0
		
		init {
			update()
		}
		
		fun update() {
			totalWeight = weights.sumOf { it.get() }
		}
		
		override fun getWeight(index: Int) = weights[index].get()
	}
}