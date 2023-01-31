package org.alc.math.vector

import org.alc.math.fix0
import java.util.*
import kotlin.math.sqrt
import org.alc.functions.*
import org.alc.math.rational.Rational
import org.alc.math.rational.toRational
import java.math.BigDecimal
import java.math.BigInteger

class RationalVector private constructor(private val values: Array<Rational>) {

    val size get() = values.size
    operator fun get(i: Int) = values[i]
    operator fun set(i: Int, value: Rational) {
        values[i] = value
    }

    companion object {
        operator fun invoke(vararg values: Rational) =
            RationalVector(Array(values.size) { i -> values[i]})

        operator fun invoke(size: Int, f: (Int) -> Rational) =
            RationalVector(Array(size, f))
    }

    operator fun plus(other: RationalVector): RationalVector {
        require(size == other.size) { "Vectors must be of the same size" }
        return RationalVector(size) { i -> values[i] + other.values[i] }
    }

    operator fun minus(other: RationalVector): RationalVector {
        require(size == other.size) { "Vectors must be of the same size" }
        return RationalVector(size) { i -> values[i] - other.values[i] }
    }

    operator fun times(other: RationalVector): Rational {
        require(size == other.size) { "Vectors must be of the same size" }
        var sum = Rational.ZERO
        for (i in 0 until size) sum += values[i] * other.values[i]
        return sum
    }

    operator fun times(other: Rational) = RationalVector(size) { i -> values[i] * other }
    operator fun times(other: Int) = this * other.toRational()
    operator fun times(other: Long) = this * other.toRational()
    operator fun times(other: Double) = this * other.toRational()
    operator fun times(other: Float) = this * other.toRational()
    operator fun times(other: BigInteger) = this * other.toRational()
    operator fun times(other: BigDecimal) = this * other.toRational()
    operator fun div(other: Rational) = RationalVector(size) { i -> values[i] / other }
    operator fun div(other: Number) = this / other.toRational()

    operator fun unaryPlus() = this
    operator fun unaryMinus() = RationalVector(size) { i -> -values[i] }

    fun normSquare() = this * this
    fun norm() = sqrt(normSquare().toDouble())

    infix fun dot(other: RationalVector): Rational = this * other

    infix fun cross(other: RationalVector): RationalVector {
        require(size == other.size && size == 3) { "Vectors must be of size 3" }
        return RationalVector(3) {
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
        if (other !is RationalVector) return false

        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        return values.contentHashCode()
    }
}
