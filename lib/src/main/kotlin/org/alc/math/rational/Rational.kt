package org.alc.math.rational

import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode

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
) : Number(), Comparable<Rational> {

    private constructor(num: Long) : this(BigInteger.valueOf(num))
    private constructor(num: Int) : this(num.toLong())
    private constructor(num: Long, den: Long) : this(BigInteger.valueOf(num), BigInteger.valueOf(den))
    private constructor(num: Int, den: Int) : this(num.toLong(), den.toLong())

    sealed interface Format
    data class Precision(val precision: Int = 10) : Format
    object Period : Format
    object Fraction : Format

    fun signum(): Int {
        return this.num.signum()
    }

    operator fun plus(other: Long) = when (other) {
        0L -> this
        else -> valueOf(this.num + BigInteger.valueOf(other) * this.den, this.den)
    }

    operator fun plus(other: Int) = plus(other.toLong())
    operator fun plus(other: Short) = plus(other.toLong())
    operator fun plus(other: Byte) = plus(other.toLong())

    operator fun plus(other: BigInteger) = when {
        other.signum() == 0 -> this
        else -> valueOf(this.num + other * this.den, this.den)
    }

    operator fun plus(other: Rational) = when {
        other.signum() == 0 -> this
        else -> valueOf(this.num * other.den + this.den * other.num, this.den * other.den)
    }

    operator fun minus(other: Long) = when (other) {
        0L -> this
        else -> valueOf(this.num - BigInteger.valueOf(other) * this.den, this.den)
    }

    operator fun minus(other: Int) = minus(other.toLong())
    operator fun minus(other: Short) = minus(other.toLong())
    operator fun minus(other: Byte) = minus(other.toLong())

    operator fun minus(other: BigInteger) = when {
        other.signum() == 0 -> this
        else -> valueOf(this.num - other * this.den, this.den)
    }

    operator fun minus(other: Rational) = when {
        other.signum() == 0 -> this
        else -> valueOf(this.num * other.den - this.den * other.num, this.den * other.den)
    }

    operator fun times(other: Long) = when (other) {
        0L -> ZERO
        1L -> this
        else -> valueOf(this.num * BigInteger.valueOf(other), this.den)
    }

    operator fun times(other: Int) = times(other.toLong())
    operator fun times(other: Short) = times(other.toLong())
    operator fun times(other: Byte) = times(other.toLong())

    operator fun times(other: BigInteger) = when {
        other.signum() == 0 -> ZERO
        other == BigInteger.ONE -> this
        else -> valueOf(this.num * other, this.den)
    }

    operator fun times(other: Rational) = when {
        other.signum() == 0 -> ZERO
        other == ONE -> this
        else -> valueOf(this.num * other.num, this.den * other.den)
    }

    operator fun div(other: Long) = when (other) {
        0L -> throw ArithmeticException("Division by 0")
        1L -> this
        else -> valueOf(this.num, this.den * BigInteger.valueOf(other))
    }

    operator fun div(other: Int) = div(other.toLong())
    operator fun div(other: Short) = div(other.toLong())
    operator fun div(other: Byte) = div(other.toLong())

    operator fun div(other: BigInteger) = when {
        other.signum() == 0 -> throw ArithmeticException("Division by 0")
        other == BigInteger.ONE -> this
        else -> valueOf(this.num, this.den * other)
    }

    operator fun div(other: Rational) = when {
        other.signum() == 0 -> throw ArithmeticException("Division by 0")
        other == ONE -> this
        else -> valueOf(this.num * other.den, this.den * other.num)
    }

    operator fun unaryPlus() = this

    operator fun unaryMinus() = when (signum()) {
        0 -> this
        else -> Rational(-this.num, this.den)
    }

    fun isZero() = signum() == 0
    fun isPositive() = signum() > 0
    fun isNegative() = signum() < 0
    fun isInteger() = den == BigInteger.ONE

    fun reciprocal() = valueOf(den, num)

    operator fun rem(other: Long) = divideAndRemainder(other).second
    fun divideAndRemainder(other: Long) = when {
        other == 0L -> throw ArithmeticException("Division by 0")
        this.signum() == 0 -> Pair(BigInteger.ZERO, this)
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
        other.signum() == 0 -> throw ArithmeticException("Division by 0")
        this.signum() == 0 -> Pair(ZERO, this)
        else -> {
            val lcm = this.den * other
            val x = this.num.divideAndRemainder(lcm)
            Pair(x[0], if (x[0] == BigInteger.ZERO) this else valueOf(x[1], this.den))
        }
    }

    operator fun rem(other: Rational) = divideAndRemainder(other).second

    fun divideAndRemainder(other: Rational): Pair<BigInteger, Rational> = when {
        other.signum() == 0 -> throw ArithmeticException("Division by 0")
        this.signum() == 0 -> Pair(BigInteger.ZERO, this)
        else -> {
            val lcm = this.den * other.den
            val x = (this.num * other.den).divideAndRemainder(other.num * this.den)
            Pair(x[0], if (x[0] == BigInteger.ZERO) this else valueOf(x[1], lcm))
        }
    }


    fun pow(exponent: Int) = when {
        exponent == 0 || this == ONE -> ONE
        exponent < 0 -> {
            if (this.signum() == 0) throw ArithmeticException("Invalid 0 raised with negative exponent")
            valueOf(this.den.pow(-exponent), this.num.pow(-exponent))
        }

        else -> valueOf(this.num.pow(exponent), this.den.pow(exponent))
    }

    fun abs() = if (this.signum() >= 0) this else Rational(-this.num, this.den)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Rational

        if (num != other.num) return false
        if (den != other.den) return false

        return true
    }

    override fun hashCode() = num.hashCode() * 31 + den.hashCode()

    override fun toByte() = toBigInteger().toByte()
    override fun toChar() = toBigInteger().toChar()
    override fun toDouble() = num.toDouble() / den.toDouble()
    override fun toFloat() = toDouble().toFloat()
    override fun toInt() = toBigInteger().toInt()
    override fun toLong() = toBigInteger().toLong()
    override fun toShort() = toBigInteger().toShort()

    fun toBigDecimal(scale: Int, roundingMode: RoundingMode) =
        num.toBigDecimal().divide(den.toBigDecimal(), scale, roundingMode)!!

    fun toBigDecimal(roundingMode: RoundingMode) =
        num.toBigDecimal().divide(den.toBigDecimal(), roundingMode)!!

    fun toBigDecimal(mc: MathContext = MathContext.DECIMAL128) =
        num.toBigDecimal().divide(den.toBigDecimal(), mc)!!


    fun toBigInteger(): BigInteger = when (this) {
        ONE -> BigInteger.ONE
        TEN -> BigInteger.TEN
        ZERO -> BigInteger.ZERO
        TWO -> BigInteger.TWO
        else -> num / den
    }

    override fun toString() = toString(Fraction)

    fun toString(format: Format) = when (den) {
        BigInteger.ONE -> num.toString()
        else -> when (format) {
            is Fraction -> "$num/$den"
            is Period, is Precision -> toFormattedString(format)
        }
    }

    private fun toFormattedString(format: Format): String {
        val num = this.num.abs()
        val buffer = StringBuilder()
        if (num.signum() != this.num.signum()) buffer.append('-')
        var (r0, r1) = num.divideAndRemainder(den)
        var pair = listOf(r0, r1)
        buffer.append(r0)
        val periodic = when (format) {
            is Period -> true
            else -> false
        }
        val n = when (format) {
            is Precision -> format.precision
            else -> 1000
        }
        if (n == 0 || r1.signum() == 0) return buffer.toString()
        buffer.append('.')

        val cache = mutableSetOf<List<BigInteger>>()
        var cycle = false
        for (i in 1..n) {
            r1 *= BigInteger.TEN
            val divideResult = r1.divideAndRemainder(den)
            r0 = divideResult[0]
            r1 = divideResult[1]
            pair = listOf(r0, r1)
            if (cache.contains(pair)) {
                cycle = true
                break
            }
            cache.add(pair)
            if (r1.signum() == 0) break
        }

        if (!cycle) {
            cache.forEach { p -> buffer.append(p[0]) }
            return buffer.toString()
        }

        var iterator = cache.iterator()
        var count = 0
        while (iterator.hasNext()) {
            val p = iterator.next()

            if (p == pair) break

            buffer.append(p[0])
            iterator.remove()
            ++count
        }

        if (periodic) {
            buffer.append('[')
            cache.forEach { p -> buffer.append(p[0]) }
            buffer.append(']')
        } else {
            buffer.append(r0)
            ++count
            while (count < n) {
                if (!iterator.hasNext()) iterator = cache.iterator()
                while (iterator.hasNext() && count < n) {
                    val k = iterator.next()[0]
                    buffer.append(k)
                    ++count
                }
            }
        }
        return buffer.toString()
    }


    override operator fun compareTo(other: Rational): Int {
        val c = signum() - other.signum()
        if (c != 0) return c
        val product1 = num * other.den
        val product2 = den * other.num
        return product1.compareTo(product2)
    }

    operator fun compareTo(other: Int): Int {
        return (this.num - (this.den * BigInteger.valueOf(other.toLong()))).signum()
    }

    operator fun compareTo(other: Long): Int {
        return (this.num - (this.den * BigInteger.valueOf(other))).signum()
    }

    operator fun compareTo(other: BigInteger): Int {
        return (this.num - (this.den * other)).signum()
    }

    companion object {

        val ZERO = Rational(BigInteger.ZERO)
        val ONE = Rational(BigInteger.ONE)
        val MINUS_ONE = Rational(-1)
        val TWO = Rational(BigInteger.TWO)
        val ONE_HALF = Rational(BigInteger.ONE, BigInteger.TWO)
        val ONE_THIRD = Rational(BigInteger.ONE, BigInteger.valueOf(3))
        val TWO_THIRDS = Rational(2, 3)
        val TEN = Rational(BigInteger.TEN)

        private val instanceCache = buildMap {
            put(ZERO, ZERO)
            put(ONE, ONE)
            put(MINUS_ONE, MINUS_ONE)
            put(TWO, TWO)
            put(ONE_HALF, ONE_HALF)
            put(ONE_THIRD, ONE_THIRD)
            put(TWO_THIRDS, TWO_THIRDS)
            put(TEN, TEN)
        }

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

        private fun canonicalValue(num: BigInteger, den: BigInteger): Rational {
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
