package org.alc.math.vector

import org.alc.math.fix0
import java.util.*
import kotlin.math.sqrt
import org.alc.functions.*

class DoubleVector private constructor(private val values: DoubleArray) {

    val size get() = values.size
    operator fun get(i: Int) = values[i]
    operator fun set(i: Int, value: Double) {
        values[i] = fix0(value)
    }

    companion object {
        operator fun invoke(vararg values: Double) =
            DoubleVector(values.map(::fix0).toDoubleArray())

        operator fun invoke(size: Int, f: (Int) -> Double) =
            DoubleVector(DoubleArray(size, f.andThen(::fix0)))

        operator fun invoke(original: DoubleVector) = DoubleVector(original.size) { i -> original[i] }
    }

    fun copy() = DoubleVector(this)

    operator fun plus(other: DoubleVector): DoubleVector {
        require(size == other.size) { "Vectors must be of the same size" }
        return DoubleVector(size) { i -> values[i] + other.values[i] }
    }

    operator fun minus(other: DoubleVector): DoubleVector {
        require(size == other.size) { "Vectors must be of the same size" }
        return DoubleVector(size) { i -> values[i] - other.values[i] }
    }

    operator fun times(other: DoubleVector): Double {
        require(size == other.size) { "Vectors must be of the same size" }
        var sum = 0.0
        for (i in 0 until size) sum += values[i] * other.values[i]
        return sum
    }

    operator fun times(other: Double) = DoubleVector(size) { i -> values[i] * other }
    operator fun times(other: Number) = this * other.toDouble()
    operator fun div(other: Double) = DoubleVector(size) { i -> values[i] / other }
    operator fun div(other: Number) = this / other.toDouble()

    operator fun unaryPlus() = this
    operator fun unaryMinus() = DoubleVector(size) { i -> -values[i] }


    fun norm() = sqrt((this * this))

    infix fun dot(other: DoubleVector): Double = this * other

    infix fun cross(other: DoubleVector): DoubleVector {
        require(size == other.size && size == 3) { "Vectors must be of size 3" }
        return DoubleVector(3) {
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
        if (other !is DoubleVector) return false

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
