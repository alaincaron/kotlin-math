package org.alc.math.rational

import org.alc.math.ring.DivisionRingElement
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

infix fun BigInteger.over(denominator: BigInteger) = Rational.valueOf(this, denominator)
infix fun Long.over(denominator: Long) = Rational.valueOf(this, denominator)
infix fun Int.over(denominator: Int) = Rational.valueOf(this, denominator)
infix fun Short.over(denominator: Short) = Rational.valueOf(this, denominator)
infix fun Byte.over(denominator: Byte) = Rational.valueOf(this, denominator)

operator fun Int.plus(other: Rational) = other.plus(this)
operator fun Long.plus(other: Rational) = other.plus(this)
operator fun BigInteger.plus(other: Rational) = other.plus(this)
operator fun Short.plus(other: Rational) = other.plus(this)
operator fun Byte.plus(other: Rational) = other.plus(this)

operator fun Int.minus(other: Rational) = Rational.valueOf(this) - other
operator fun Long.minus(other: Rational) = Rational.valueOf(this) - other
operator fun BigInteger.minus(other: Rational) = Rational.valueOf(this) - other
operator fun Short.minus(other: Rational) = Rational.valueOf(this) - other
operator fun Byte.minus(other: Rational) = Rational.valueOf(this) - other

operator fun Int.times(other: Rational) = other * this
operator fun Long.times(other: Rational) = other * this
operator fun BigInteger.times(other: Rational) = other * this
operator fun Short.times(other: Rational) = other * this
operator fun Byte.times(other: Rational) = other * this

operator fun Int.div(other: Rational) = Rational.valueOf(this) / other
operator fun Long.div(other: Rational) = Rational.valueOf(this) / other
operator fun BigInteger.div(other: Rational) = Rational.valueOf(this) / other
operator fun Short.div(other: Rational) = Rational.valueOf(this) / other
operator fun Byte.div(other: Rational) = Rational.valueOf(this) / other

operator fun Int.rem(other: Rational) = Rational.valueOf(this) % other
operator fun Long.rem(other: Rational) = Rational.valueOf(this) % other
operator fun BigInteger.rem(other: Rational) = Rational.valueOf(this) % other
operator fun Short.rem(other: Rational) = Rational.valueOf(this) % other
operator fun Byte.rem(other: Rational) = Rational.valueOf(this) % other

operator fun Int.compareTo(other: Rational) = Rational.valueOf(this).compareTo(other)
operator fun Long.compareTo(other: Rational) = Rational.valueOf(this).compareTo(other)
operator fun BigInteger.compareTo(other: Rational) = Rational.valueOf(this).compareTo(other)
operator fun Short.compareTo(other: Rational) = Rational.valueOf(this).compareTo(other)
operator fun Byte.compareTo(other: Rational) = Rational.valueOf(this).compareTo(other)

class Rational private constructor(
    val num: BigInteger,
    val den: BigInteger = BigInteger.ONE
) : Number(), DivisionRingElement<Rational>, Comparable<Rational> {

    sealed interface Format

    sealed class Radix(val precision: Int = 10, _radix: Int = 10) : Format {
        init {
            if (_radix < 2 || _radix > 36) throw IllegalArgumentException("Invalid base must be between 2 and 36")
        }

        val radix: BigInteger = when (_radix) {
            10 -> BigInteger.TEN
            2 -> BigInteger.TWO
            else -> BigInteger.valueOf(_radix.toLong())
        }
    }

    open class Precision(precision: Int = 10, radix: Int = 10) : Radix(precision, radix)
    class Periodic(precision: Int = 10, radix: Int = 10) : Radix(precision, radix)

    class Binary(precision: Int = 20) : Precision(precision, 2)
    class Octal(precision: Int = 20) : Precision(precision, 8)

    class Hexadecimal(precision: Int = 10) : Precision(precision, 16)
    object Fraction : Format
    object MixedFraction : Format

    operator fun plus(other: Long) = when {
        isNaN() -> NaN
        other == 0L -> this
        else -> valueOf(this.num + BigInteger.valueOf(other) * this.den, this.den)
    }

    operator fun plus(other: Int) = plus(other.toLong())
    operator fun plus(other: Short) = plus(other.toLong())
    operator fun plus(other: Byte) = plus(other.toLong())

    operator fun plus(other: BigInteger) = when {
        isNaN() -> this
        other.signum() == 0 -> this
        else -> valueOf(this.num + other * this.den, this.den)
    }

    override operator fun plus(other: Rational) = when {
        other.isNaN() || isNaN() -> NaN
        other == ZERO -> this
        this == ZERO -> other
        else -> valueOf(this.num * other.den + this.den * other.num, this.den * other.den)
    }

    operator fun minus(other: Long) = when {
        isNaN() -> this
        other == 0L -> this
        else -> valueOf(this.num - BigInteger.valueOf(other) * this.den, this.den)
    }

    operator fun minus(other: Int) = minus(other.toLong())
    operator fun minus(other: Short) = minus(other.toLong())
    operator fun minus(other: Byte) = minus(other.toLong())

    operator fun minus(other: BigInteger) = when {
        isNaN() -> this
        other.signum() == 0 -> this
        else -> valueOf(this.num - other * this.den, this.den)
    }

    override operator fun minus(other: Rational) = when {
        isNaN() || other.isNaN() -> NaN
        other.isZero() -> this
        this.isZero() -> other.negate()
        else -> valueOf(this.num * other.den - this.den * other.num, this.den * other.den)
    }

    operator fun times(other: Long) = when {
        isNaN() || isZero() -> this
        other == 0L -> ZERO
        other == 1L -> this
        else -> valueOf(this.num * BigInteger.valueOf(other), this.den)
    }

    operator fun times(other: Int) = times(other.toLong())
    operator fun times(other: Short) = times(other.toLong())
    operator fun times(other: Byte) = times(other.toLong())

    operator fun times(other: BigInteger) = when {
        isNaN() -> this
        other.signum() == 0 -> ZERO
        other == BigInteger.ONE -> this
        else -> valueOf(this.num * other, this.den)
    }

    override operator fun times(other: Rational) = when {
        isNaN() || other.isNaN() -> NaN
        isZero() || other.isZero() -> ZERO
        other == ONE -> this
        else -> valueOf(this.num * other.num, this.den * other.den)
    }

    operator fun div(other: Long) = when {
        other == 0L -> throw ArithmeticException("Division by 0")
        isNaN() -> this
        other == 1L -> this
        else -> valueOf(this.num, this.den * BigInteger.valueOf(other))
    }

    operator fun div(other: Int) = div(other.toLong())
    operator fun div(other: Short) = div(other.toLong())
    operator fun div(other: Byte) = div(other.toLong())

    operator fun div(other: BigInteger) = when {
        other.signum() == 0 -> throw ArithmeticException("Division by 0")
        isNaN() -> this
        isZero() -> this
        other == BigInteger.ONE -> this
        else -> valueOf(this.num, this.den * other)
    }

    override operator fun div(den: Rational) = when {
        den.isZero() -> throw ArithmeticException("Division by 0")
        isNaN() || den.isNaN() -> NaN
        den == ONE -> this
        else -> valueOf(this.num * den.den, this.den * den.num)
    }

    operator fun unaryPlus() = this

    operator fun unaryMinus() = when (num.signum()) {
        0 -> this // covers both 0 and NaN
        else -> canonicalValue(-this.num, this.den)
    }

    fun negate() = unaryMinus()

    fun isNaN() = num.signum() == 0 && den.signum() == 0
    fun isZero() = num.signum() == 0 && den.signum() != 0
    fun isPositive() = num.signum() > 0
    fun isNegative() = num.signum() < 0
    fun isInteger() = den == BigInteger.ONE

    fun reciprocal() = if (isNaN()) this else valueOf(den, num)

    operator fun rem(other: Long) = divideAndRemainder(other).second
    fun divideAndRemainder(other: Long) = when {
        isNaN() -> Pair(BigInteger.ZERO, this)
        other == 0L -> throw ArithmeticException("Division by 0")
        isZero() -> Pair(BigInteger.ZERO, this)
        else -> {
            val lcm = this.den * BigInteger.valueOf(other)
            val x = this.num.divideAndRemainder(lcm)
            Pair(x[0], if (x[0] == BigInteger.ZERO) this else valueOf(x[1], this.den))
        }
    }

    operator fun rem(other: Int) = divideAndRemainder(other).second
    fun divideAndRemainder(other: Int) = divideAndRemainder(other.toLong())

    operator fun rem(other: Short) = divideAndRemainder(other).second
    fun divideAndRemainder(other: Short) = divideAndRemainder(other.toLong())

    operator fun rem(other: Byte) = divideAndRemainder(other).second
    fun divideAndRemainder(other: Byte) = divideAndRemainder(other.toLong())

    operator fun rem(other: BigInteger) = divideAndRemainder(other).second
    fun divideAndRemainder(other: BigInteger) = when {
        isNaN() -> Pair(this, this)
        other.signum() == 0 -> throw ArithmeticException("Division by 0")
        isZero() -> Pair(ZERO, this)
        else -> {
            val lcm = this.den * other
            val x = this.num.divideAndRemainder(lcm)
            Pair(x[0], if (x[0] == BigInteger.ZERO) this else valueOf(x[1], this.den))
        }
    }

    override operator fun rem(den: Rational) = divideAndRemainder(den).second

    fun divideAndRemainder(other: Rational): Pair<BigInteger, Rational> = when {
        isNaN() || other.isNaN() -> Pair(BigInteger.ZERO, NaN)
        other.isZero() -> throw ArithmeticException("Division by 0")
        isZero() -> Pair(BigInteger.ZERO, this)
        else -> {
            val lcm = this.den * other.den
            val x = (this.num * other.den).divideAndRemainder(other.num * this.den)
            Pair(x[0], if (x[0] == BigInteger.ZERO) this else valueOf(x[1], lcm))
        }
    }


    fun pow(exponent: Int) = when {
        isNaN() -> this
        exponent == 0 || this == ONE -> ONE
        exponent < 0 -> {
            if (isZero()) throw ArithmeticException("Invalid 0 raised with negative exponent")
            valueOf(this.den.pow(-exponent), this.num.pow(-exponent))
        }

        else -> valueOf(this.num.pow(exponent), this.den.pow(exponent))
    }

    fun abs() = when {
        num.signum() >= 0 -> this // covers both 0 and NaN
        else -> canonicalValue(-this.num, this.den)
    }

    fun ceil() = when {
        isNaN() -> this
        den == BigInteger.ONE -> this
        isPositive() -> canonicalValue((num / den) + BigInteger.ONE)
        else -> canonicalValue(this.num / this.den)
    }

    fun floor() = when {
        isNaN() -> this
        den == BigInteger.ONE -> this
        isPositive() -> canonicalValue((num / den))
        else -> canonicalValue(num / den - BigInteger.ONE)
    }

    private fun roundPositive(num: BigInteger, den: BigInteger) = (num + den / BigInteger.TWO) / den

    fun round() = when {
        isNaN() -> this
        den == BigInteger.ONE -> this
        isPositive() -> canonicalValue(roundPositive(num, den))
        den.mod(BigInteger.TWO) == BigInteger.ZERO ->
            canonicalValue(
                roundPositive(
                    -num - BigInteger.ONE, den
                ).negate()
            )

        else ->
            canonicalValue(roundPositive(-num, den).negate())
    }

    override fun equals(other: Any?): Boolean {
        if (isNaN()) return false
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (other.isNaN()) return false
        if (num != other.num) return false
        if (den != other.den) return false

        return true
    }

    override fun hashCode() = num.hashCode() * 31 + den.hashCode()

    override fun toByte() = toBigInteger().toByte()
    override fun toChar() = toBigInteger().toChar()
    override fun toDouble() = if (isNaN()) Double.NaN else num.toDouble() / den.toDouble()
    override fun toFloat() = toDouble().toFloat()
    override fun toInt() = toBigInteger().toInt()
    override fun toLong() = toBigInteger().toLong()
    override fun toShort() = toBigInteger().toShort()

    fun toBigDecimal(scale: Int, roundingMode: RoundingMode): BigDecimal {
        if (isNaN()) throw ArithmeticException("NaN conversion")
        return num.toBigDecimal().divide(den.toBigDecimal(), scale, roundingMode)
    }

    fun toBigDecimal(roundingMode: RoundingMode): BigDecimal {
        if (isNaN()) throw ArithmeticException("NaN conversion")
        return num.toBigDecimal().divide(den.toBigDecimal(), roundingMode)
    }

    fun toBigDecimal(mc: MathContext = MathContext.DECIMAL128): BigDecimal {
        if (isNaN()) throw ArithmeticException("NaN conversion")
        return num.toBigDecimal().divide(den.toBigDecimal(), mc)
    }

    fun toBigInteger(): BigInteger {
        if (isNaN()) throw ArithmeticException("NaN conversion")
        return when (this) {
            ONE -> BigInteger.ONE
            TEN -> BigInteger.TEN
            ZERO -> BigInteger.ZERO
            TWO -> BigInteger.TWO
            else -> num / den
        }
    }

    override fun toString() = toStringBuilder().toString()

    fun toString(format: Format) = toStringBuilder(format).toString()

    fun toStringBuilder(format: Format, builder: StringBuilder? = null): StringBuilder {
        val b = builder ?: StringBuilder()
        if (isNaN()) return b.append("NaN")
        return when (format) {
            is Radix -> toStringBuilder1(format, b)
            is Fraction -> formatFraction(b)
            is MixedFraction -> formatMixedFraction(b)
        }
    }

    fun toStringBuilder(builder: StringBuilder? = null) = toStringBuilder(FRACTION, builder)

    private fun formatFraction(builder: StringBuilder) = when {
        isInteger() -> builder.append(num)
        else -> builder.append(num).append('/').append(den)
    }

    private fun formatMixedFraction(builder: StringBuilder): StringBuilder {
        if (isInteger()) return builder.append(num)
        val intPart = num / den
        if (intPart.signum() == 0) return builder.append(num).append('/').append(den)
        builder.append(intPart).append(" ")
        val residual = (num - intPart * den).abs()
        return builder.append(residual).append('/').append(den)
    }

    private fun toRadix(x: BigInteger) = when (val v = x.toInt()) {
        in 0..9 -> (v + '0'.code).toChar()
        else -> (v - 10 + 'A'.code).toChar()
    }

    private fun toStringBuilder1(format: Radix, builder: StringBuilder): StringBuilder {
        if (isInteger()) {
            return builder.append(this.num.toString(format.radix.toInt()))
        }
        val num = this.num.abs()
        if (num.signum() != this.num.signum()) builder.append('-')
        var (r0, r1) = num.divideAndRemainder(den)
        builder.append(r0.toString(format.radix.toInt()))
        val n = format.precision
        if (n == 0 || r1.signum() == 0) return builder
        builder.append('.')

        val cache = mutableSetOf<Pair<Char, BigInteger>>()
        var cycle = false
        var pair: Pair<Char, BigInteger>? = null
        for (i in 1..n) {
            r1 *= format.radix
            val divideResult = r1.divideAndRemainder(den)
            r1 = divideResult[1]
            pair = Pair(toRadix(divideResult[0]), r1)
            if (cache.contains(pair)) {
                cycle = true
                break
            }
            cache.add(pair)
            if (r1.signum() == 0) break
        }

        if (!cycle) {
            cache.forEach { p -> builder.append(p.first) }
            return builder
        }

        var iterator = cache.iterator()
        var count = 0
        while (iterator.hasNext()) {
            val p = iterator.next()

            if (p == pair) break

            builder.append(p.first)
            iterator.remove()
            ++count
        }

        if (format is Periodic) {
            builder.append('[')
            cache.forEach { p -> builder.append(p.first) }
            builder.append(']')
        } else {
            builder.append(pair!!.first)
            ++count
            while (count < n) {
                if (!iterator.hasNext()) iterator = cache.iterator()
                while (iterator.hasNext() && count < n) {
                    builder.append(iterator.next().first)
                    ++count
                }
            }
        }
        return builder
    }


    override operator fun compareTo(other: Rational): Int {
        if (isNaN()) return if (other.isNaN()) 0 else 1
        if (other.isNaN()) return -1
        val c = num.signum() - other.num.signum()
        if (c != 0) return if (c > 0) 1 else -1
        val product1 = num * other.den
        val product2 = den * other.num
        return product1.compareTo(product2)
    }

    operator fun compareTo(other: Int): Int {
        if (isNaN()) return 1
        return (this.num - (this.den * BigInteger.valueOf(other.toLong()))).signum()
    }

    operator fun compareTo(other: Long): Int {
        if (isNaN()) return 1
        return (this.num - (this.den * BigInteger.valueOf(other))).signum()
    }

    operator fun compareTo(other: BigInteger): Int {
        if (isNaN()) return 1
        return (this.num - (this.den * other)).signum()
    }

    fun min(other: Rational) = when {
        isNaN() || other.isNaN() -> NaN
        else -> if (compareTo(other) < 0) this else other
    }

    fun max(other: Rational) = when {
        isNaN() || other.isNaN() -> NaN
        else -> if (compareTo(other) > 0) this else other
    }

    companion object {

        val FRACTION = Fraction
        val PERIODIC = Periodic()
        val PRECISION = Precision()
        val MIXED = MixedFraction
        val HEXADECIMAL = Hexadecimal()
        val OCTAL = Octal()
        val BINARY = Binary()

        private val instanceCache = mutableMapOf<Rational, Rational>()

        private fun store(x: Rational): Rational {
            instanceCache[x] = x
            return x
        }

        val NaN = store(Rational(BigInteger.ZERO, BigInteger.ZERO))
        val ZERO = store(Rational(BigInteger.ZERO))
        val ONE = store(Rational(BigInteger.ONE))
        val MINUS_ONE = store(Rational(BigInteger.valueOf(-1)))
        val TWO = store(Rational(BigInteger.TWO))
        val ONE_HALF = store(Rational(BigInteger.ONE, BigInteger.TWO))
        val ONE_THIRD = store(Rational(BigInteger.ONE, BigInteger.valueOf(3)))
        val TWO_THIRDS = store(Rational(BigInteger.TWO, BigInteger.valueOf(3)))
        val TEN = store(Rational(BigInteger.TEN))

        fun valueOf(num: Byte, den: Byte = 1) =
            valueOf(BigInteger.valueOf(num.toLong()), BigInteger.valueOf(den.toLong()))

        fun valueOf(num: Short, den: Short = 1) =
            valueOf(BigInteger.valueOf(num.toLong()), BigInteger.valueOf(den.toLong()))

        fun valueOf(num: Int, den: Int = 1) =
            valueOf(BigInteger.valueOf(num.toLong()), BigInteger.valueOf(den.toLong()))

        fun valueOf(num: Long, den: Long = 1L) =
            valueOf(BigInteger.valueOf(num), BigInteger.valueOf(den))

        fun valueOf(value: BigDecimal): Rational {
            val scale = value.scale()
            val unscaledValue = value.unscaledValue()
            if (scale == 0) return valueOf(unscaledValue)
            if (scale > 0) return valueOf(unscaledValue, BigInteger.TEN.pow(scale))
            return valueOf(unscaledValue * BigInteger.TEN.pow(-scale))
        }

        private fun canonicalValue(num: BigInteger, den: BigInteger = BigInteger.ONE): Rational {
            val r = Rational(num, den)
            val cached = instanceCache[r]
            return cached ?: r
        }

        fun valueOf(numerator: BigInteger, denominator: BigInteger = BigInteger.ONE): Rational {
            if (denominator.signum() == 0) throw ArithmeticException("Invalid 0 denominator")
            if (numerator.signum() == 0) return ZERO
            var num = numerator
            var den = denominator

            val gcd = num.gcd(den)
            if (gcd > BigInteger.ONE) {
                num /= gcd
                den /= gcd
            }

            if (den.signum() < 0) {
                num = -num
                den = -den
            }
            return canonicalValue(num, den)
        }
    }
}

fun max(value: Rational, vararg values: Rational) = values.fold(value) { acc, i -> acc.max(i) }
fun min(value: Rational, vararg values: Rational) = values.fold(value) { acc, i -> acc.min(i) }

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


