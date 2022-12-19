package org.alc.math.rational

import java.math.BigDecimal
import java.math.BigInteger

fun Number.toRational(): Rational =
    when (this) {
        is Rational -> this
        is BigInteger -> toRational()
        is Long -> toLong().toRational()
        is Int -> toInt().toRational()
        is Short -> toShort().toRational()
        is Byte -> toByte().toRational()
        is Float -> toFloat().toRational()
        is Double -> toDouble().toRational()
        is BigDecimal -> toRational()
        else -> toString().toRational()
    }

fun BigInteger.toRational() = Rational.valueOf(this)
fun Long.toRational() = Rational.valueOf(this)
fun Int.toRational() = Rational.valueOf(this)
fun Short.toRational() = Rational.valueOf(this)
fun Byte.toRational() = Rational.valueOf(this)
fun Float.toRational() = toDouble().toRational()
fun Double.toRational(): Rational {
    require(!isNaN() && isFinite()) { "Cannot convert NaN or infinite values to a rational number" }

    return when (this) {
        0.0 -> Rational.ZERO
        1.0 -> Rational.ONE
        2.0 -> Rational.TWO
        10.0 -> Rational.TEN
        else -> toBigDecimal().toRational()
    }
}

fun BigDecimal.toRational() = Rational.valueOf(this)

fun String.toRational(): Rational {
    val dividePos = indexOf('/')
    if (dividePos < 0) return BigDecimal(this).toRational()
    val numString = substring(0, dividePos)
    val spacePos = numString.indexOf(' ')
    if (spacePos < 0) {
        val num = BigInteger(numString)
        val den = BigInteger(substring(dividePos + 1))
        return Rational.valueOf(num, den)
    }
    val integerPart = BigInteger(substring(0, spacePos))
    val num = BigInteger(substring(spacePos + 1, dividePos))
    val den = BigInteger(substring(dividePos + 1))
    val fraction = Rational.valueOf(num, den)
    return if (integerPart.signum() < 0) integerPart - fraction else integerPart + fraction
}


