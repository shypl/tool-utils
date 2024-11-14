package org.shypl.tool.utils.math

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.function.Supplier

class WeightRandomness<T>(
	private val values: List<T>,
	private val weights: WeightRandomizer
) : Supplier<T> {
	
	constructor(values: List<T>, weights: IntArray) : this(values, WeightRandomizer.Fixed(weights))
	constructor(values: List<T>, weights: List<Int>) : this(values, WeightRandomizer.Fixed(weights))
	constructor(pairs: Collection<Pair<T, Int>>) : this(pairs.map { it.first }, pairs.map { it.second })
	
	@JsonCreator
	constructor(pairs: Map<T, Int>) : this(pairs.toList())
	
	override fun get(): T {
		return values[weights.nextIndex()]
	}
}

fun <T> Collection<Pair<T, Int>>.randomize(): Supplier<T> {
	return WeightRandomness(this)
}

fun <T> Map<T, Int>.randomize(): Supplier<T> {
	return WeightRandomness(this)
}