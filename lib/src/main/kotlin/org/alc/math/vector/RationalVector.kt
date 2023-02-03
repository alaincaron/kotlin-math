package org.alc.math.vector

import org.alc.math.polynomial.Polynomial
import org.alc.math.rational.Rational
import org.alc.math.rational.toRational
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.sqrt


operator fun Array<Rational>.times(other: Int) = this * other.toRational()
operator fun Array<Rational>.times(other: Long) = this * other.toRational()
operator fun Array<Rational>.times(other: Double) = this * other.toRational()
operator fun Array<Rational>.times(other: Float) = this * other.toRational()
operator fun Array<Rational>.times(other: BigInteger) = this * other.toRational()
operator fun Array<Rational>.times(other: BigDecimal) = this * other.toRational()

operator fun Number.times(other: Array<Rational>) = other * this.toRational()

fun Array<Rational>.norm() = sqrt(normSquare().toDouble())

object RationalVector {
    fun of(vararg values: Number) =
        Array(values.size) { i -> values[i].toRational() }

    operator fun invoke(size: Int, f: (Int) -> Rational) = Array(size, f)
    operator fun invoke(size: Int, value: Rational) = Array(size) { value }
    operator fun invoke(size: Int) = Array(size) { Rational.ZERO }
}
