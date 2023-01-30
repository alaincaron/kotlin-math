package org.alc.math.vector

import java.util.*
import kotlin.math.sqrt

operator fun Int.times(other: IntVector) = other * this

class IntVector private constructor(private val values: IntArray) {

    val size get() = values.size
    operator fun get(i: Int) = values[i]
    operator fun set(i: Int, value: Int) {
        values[i] = value
    }

    companion object {
        operator fun invoke(vararg values: Int) = IntVector(values.copyOf())
        operator fun invoke(size: Int, f: (Int) -> Int) = IntVector(IntArray(size, f))
    }

    operator fun plus(other: IntVector): IntVector {
        require(size == other.size) { "Vectors must be of the same size" }
        val newValues = IntArray(size) { i -> values[i] + other.values[i] }
        return IntVector(newValues)
    }

    operator fun minus(other: IntVector): IntVector {
        require(size == other.size) { "Vectors must be of the same size" }
        val newValues = IntArray(size) { i -> values[i] - other.values[i] }
        return IntVector(newValues)
    }

    operator fun times(other: IntVector): Int {
        require(size == other.size) { "Vectors must be of the same size" }
        var sum = 0
        for (i in 0 until size) sum += values[i] * other.values[i]
        return sum
    }

    operator fun times(other: Int) = IntVector(size) { i -> other * values[i] }

    operator fun unaryPlus() = this
    operator fun unaryMinus() = IntVector(size) { i -> -values[i] }


    fun norm() = sqrt((this * this).toDouble())

    infix fun dot(other: IntVector): Int = this * other

    infix fun cross(other: IntVector): IntVector {
        require(size == other.size && size == 3) { "Vectors must of size 3" }
        return IntVector(3) {
            when (it) {
                0 -> (get(1) * other[2]) - (get(2) * other[1])
                1 -> (get(2) * other[0]) - (get(0) * other[2])
                else -> (get(0) * other[1]) - (get(1) * other[0])
            }
        }
    }

    override fun toString(): String = values.fold(
        StringJoiner(", ", "[ ", " ]")
    ) { joiner, x -> joiner.add(x.toString()) }.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IntVector) return false

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}


