package org.alc.math.rational

import org.junit.jupiter.api.assertThrows
import java.lang.StringBuilder
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.test.*

class RationalTest {

    @Test
    fun cachedValue() {
        assertSame(Rational.ZERO, 0 over 1000)
        assertSame(Rational.ONE, 2 over 2)
        assertSame(Rational.TEN, 100 over 10)
        assertSame(Rational.MINUS_ONE, -1 over 1)
        assertSame(Rational.ONE_HALF, 1 over 2)
        assertSame(Rational.TWO, 6 over 3)
        assertSame(Rational.ONE_THIRD, 1 over 3)
        assertSame(Rational.TWO_THIRDS, 4 over 6)
        assertSame(Rational.NaN, Rational.ONE + Rational.NaN)
    }

    @Test
    fun isZero() {
        assertTrue(Rational.ZERO.isZero())
        assertFalse(Rational.ONE.isZero())
        assertFalse(Rational.NaN.isZero())
    }

    @Test
    fun isPositive() {
        assertTrue(Rational.ONE.isPositive())
        assertFalse(Rational.MINUS_ONE.isPositive())
        assertFalse(Rational.ZERO.isPositive())
        assertFalse(Rational.NaN.isPositive())
    }

    @Test
    fun isNegative() {
        assertTrue(Rational.MINUS_ONE.isNegative())
        assertFalse(Rational.ONE.isNegative())
        assertFalse(Rational.ZERO.isNegative())
        assertFalse(Rational.NaN.isNegative())
    }

    @Test fun isNaN() {
        assertFalse(Rational.ZERO.isNaN())
        assertTrue(Rational.NaN.isNaN())
    }

    @Test
    fun isInteger() {
        assertTrue(Rational.MINUS_ONE.isInteger())
        assertTrue(Rational.ONE.isInteger())
        assertTrue(Rational.ZERO.isInteger())
        assertFalse(Rational.ONE_HALF.isInteger())
        assertFalse((-1 over 2).isInteger())
        assertFalse(Rational.NaN.isInteger())

    }

    @Test
    fun reciprocal() {
        assertThrows<ArithmeticException> { Rational.ZERO.reciprocal() }
        assertSame(Rational.ONE, Rational.ONE.reciprocal())
        assertSame(Rational.ONE_HALF, Rational.TWO.reciprocal())
        assertSame(Rational.TWO, Rational.ONE_HALF.reciprocal())
        assertEquals((5 over 2), (2 over 5).reciprocal())
        assertSame(Rational.NaN, Rational.NaN.reciprocal())
    }

    @Test
    fun addRational() {
        val a = Rational.ONE_HALF
        val b = 1 over 3
        assertEquals(5 over 6, a + b)
        assertSame(Rational.NaN, Rational.NaN + a)
        assertSame(Rational.NaN, a + Rational.NaN)
    }

    @Test
    fun addBigInteger() {
        val a = Rational.ONE_HALF
        val b = BigInteger.valueOf(2)
        assertEquals(5 over 2, a + b)
        assertSame(Rational.NaN, Rational.NaN + b)
        assertSame(Rational.NaN, b + Rational.NaN)
    }

    @Test
    fun addLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(5 over 2, a + b)
        assertSame(Rational.NaN, Rational.NaN + b)
        assertSame(Rational.NaN, b + Rational.NaN)
    }

    @Test
    fun addInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(5 over 2, a + b)
        assertSame(Rational.NaN, Rational.NaN + b)
        assertSame(Rational.NaN, b + Rational.NaN)
    }

    @Test
    fun add0() {
        val a = 1 over 2
        assertSame(a, a + 0)
        assertSame(a, a + 0L)
        assertSame(a, a + BigInteger.ZERO)
        assertSame(a, a + Rational.ZERO)
        assertSame(Rational.NaN, Rational.NaN + Rational.ZERO)
        assertSame(Rational.NaN, BigInteger.ZERO + Rational.NaN)
    }

    @Test
    fun subtractRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(1 over 6, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)
    }

    @Test
    fun subtractBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        assertEquals(-3 over 2, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)
    }

    @Test
    fun subtractLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(-3 over 2, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)
    }

    @Test
    fun subtractInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(-3 over 2, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)
    }

    @Test
    fun subtract0() {
        val a = 1 over 2
        assertSame(a, a - 0)
        assertSame(a, a - 0L)
        assertSame(a, a - BigInteger.ZERO)
        assertSame(a, a - Rational.ZERO)
        assertEquals(a.negate(), Rational.ZERO - a)
        assertSame(Rational.NaN, Rational.NaN - Rational.ZERO)
        assertSame(Rational.NaN, Rational.ZERO - Rational.NaN)
    }

    @Test
    fun multiplyRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(1 over 6, a * b)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)
    }

    @Test
    fun multiplyBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        assertSame(Rational.ONE, a * b)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)
    }

    @Test
    fun multiplyLong() {
        val a = 3 over 2
        val b = 2L
        assertEquals(Rational.ONE * 3, a * b)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)
    }

    @Test
    fun multiplyInt() {
        val a = 1 over 2
        val b = 2
        assertSame(Rational.ONE, a * b)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)
    }

    @Test
    fun multiplyBy0() {
        val a = 1.toByte() over 2.toByte()
        assertSame(Rational.ZERO, a * 0)
        assertSame(Rational.ZERO, a * 0L)
        assertSame(Rational.ZERO, a * BigInteger.ZERO)
        assertSame(Rational.ZERO, a * Rational.ZERO)
        assertSame(Rational.NaN, Rational.NaN * Rational.ZERO)
    }

    @Test
    fun multiplyBy1() {
        val a = 1.toShort() over 2.toShort()
        assertSame(a, a * 1)
        assertSame(a, a * 1L)
        assertSame(a, a * BigInteger.ONE)
        assertSame(a, a * Rational.ONE)
    }

    @Test
    fun divideRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(3 over 2, a / b)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)
    }

    @Test
    fun divideBigInteger() {
        val a = Rational.valueOf(2)
        val b = BigInteger.valueOf(2L)
        assertSame(Rational.ONE, a / b)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)
    }

    @Test
    fun divideLong() {
        val a = 3 over 2
        val b = 2L
        assertEquals(3 over 4, a / b)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)
    }

    @Test
    fun divideInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(1 over 4, a / b)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)
    }

    @Test
    fun divideBy0() {
        val a = 1 over 2
        assertThrows<ArithmeticException> { a / 0 }
        assertThrows<ArithmeticException> { a / 0L }
        assertThrows<ArithmeticException> { a / BigInteger.ZERO }
        assertThrows<ArithmeticException> { a / Rational.ZERO }
        assertThrows<ArithmeticException> { Rational.NaN / Rational.ZERO}
    }

    @Test
    fun divideBy1() {
        val a = 1 over 2
        assertSame(a, a / 1)
        assertSame(a, a / 1L)
        assertSame(a, a / BigInteger.ONE)
        assertSame(a, a / Rational.ONE)
        assertSame(Rational.NaN, Rational.NaN / 1)
        assertSame(Rational.NaN, 1 / Rational.NaN)
    }

    @Test
    fun compareTo() {
        val a = -1 over 2
        val b = -1 over 3
        val c = -b
        val d = -a
        assertTrue(a < b)
        assertTrue(b > a)
        assertTrue(a < Rational.ZERO)
        assertTrue(Rational.ZERO > b)
        assertTrue(a <= b)
        assertTrue(a < c)
        assertTrue(c > Rational.ZERO)
        assertTrue(Rational.ZERO <= Rational.ZERO)
        assertTrue(c >= a)
        assertTrue(c <= d)
        assertTrue(d > c)

        assertTrue(a < 0)
        assertTrue(a < 0L)
        assertTrue(a < BigInteger.ZERO)

        assertTrue(0 < d)
        assertTrue(0L < d)
        assertTrue(BigInteger.ZERO < d)

        assertTrue(d < 1)
        assertTrue(d < 1L)
        assertTrue(d < BigInteger.ONE)
        assertTrue(d < Rational.ONE)

        assertTrue(d < Rational.NaN)
        assertTrue(Rational.NaN > d)

        assertFalse(Rational.NaN < Rational.NaN)
        assertTrue(Rational.NaN <= Rational.NaN)
        assertFalse(Rational.NaN > Rational.NaN)
        assertTrue(Rational.NaN >= Rational.NaN)
    }

    @Test
    fun testEquals() {
        assertTrue(Rational.valueOf(1,3).equals(1 over 3))
        assertFalse((1 over 2).equals(1 over 3))
        assertFalse(Rational.NaN.equals(Rational.NaN))
        assertFalse(Rational.ONE.equals(Rational.NaN))
        assertFalse(Rational.NaN.equals(Rational.ONE))
    }

    @Test
    fun remainder() {
        val a = 1L over 2L
        val b = BigInteger.valueOf(1) over BigInteger.valueOf(3)
        val c = 1 over 4
        assertEquals(1 over 6, a % b)
        assertSame(Rational.ZERO, a % c)
        assertEquals(Rational.ONE, Rational.valueOf(5) % Rational.valueOf(2))
        assertEquals(2 over 3, (8 over 3) % 2)
        assertEquals(Rational.ONE, Rational.valueOf(5) % 2L)
        assertSame(Rational.ONE, Rational.valueOf(5) % BigInteger.valueOf(2L))
        assertSame(Rational.NaN, a % Rational.NaN)
        assertSame(Rational.NaN, Rational.NaN % a)
    }

    @Test
    fun power() {
        val a = 2 over 3
        assertSame(Rational.ONE, a.pow(0))
        assertEquals(4 over 9, a.pow(2))
        assertEquals(3 over 2, a.pow(-1))
        assertSame(Rational.NaN, Rational.NaN.pow(2))
    }

    @Test
    fun abs() {
        assertSame(Rational.ZERO, Rational.ZERO.abs())
        assertSame(Rational.ONE, Rational.ONE.abs())
        val a = 1 over 2
        assertSame(a, a.abs())
        val b = a.negate()
        assertSame(Rational.ZERO, a + b)
        assertEquals(a, b.abs())

        assertSame(Rational.NaN, Rational.NaN.abs())
    }

    @Test
    fun toStringTest() {
        assertEquals("0", Rational.ZERO.toString())
        assertEquals("10", Rational.TEN.toString())
        assertEquals("NaN", Rational.NaN.toString())
    }

    @Test
    fun toStringPrecisionTest() {
        assertEquals("0.3333333333", (1 over 3).toString(Rational.PRECISION))
        assertEquals("0.011", (1 over 90).toString(Rational.Precision(3)))
        assertEquals("0", (1 over 5).toString(Rational.Precision(0)))
        assertEquals("0.1428571428", (1 over 7).toString(Rational.PRECISION))
        assertEquals("0.5", (1 over 2).toString(Rational.PRECISION))
        assertEquals("10", Rational.TEN.toString(Rational.PRECISION))
        assertEquals(Math.PI.toString(), Math.PI.toRational().toString(Rational.Precision(15)))
        assertEquals("NaN", Rational.NaN.toString(Rational.PRECISION))
    }

    @Test
    fun toStringPeriodTest() {
        assertEquals("0.[3]", (1 over 3).toString(Rational.PERIODIC))
        assertEquals("0.0[1]", (1 over 90).toString(Rational.PERIODIC))
        assertEquals("0.[142857]", (1 over 7).toString(Rational.PERIODIC))
        assertEquals("0.000[142857]", (1 over 7000).toString(Rational.PERIODIC))
        assertEquals("0.5", (1 over 2).toString(Rational.PERIODIC))
        assertEquals("10", Rational.TEN.toString(Rational.PERIODIC))
        assertEquals(Math.PI.toString(), Math.PI.toRational().toString(Rational.Periodic(15)))
        assertEquals("NaN", Rational.NaN.toString(Rational.PERIODIC))
    }

    @Test
    fun toBigIntegerTest() {
        assertSame(BigInteger.ONE, Rational.ONE.toBigInteger())
        assertSame(BigInteger.TWO, Rational.TWO.toBigInteger())
        assertEquals(BigInteger.ONE, (3 over 2).toBigInteger())
        assertThrows<ArithmeticException> { Rational.NaN.toBigInteger()  }
    }

    @Test
    fun toBigDecimalTest() {
        assertEquals(BigDecimal.valueOf(1.5), (3 over 2).toBigDecimal())
        assertEquals(BigDecimal.valueOf(150, 2), (3 over 2).toBigDecimal(2, RoundingMode.UNNECESSARY))
        assertEquals(BigDecimal.valueOf(2), (3 over 2).toBigDecimal(RoundingMode.UP))
        assertThrows<ArithmeticException> { Rational.NaN.toBigDecimal()  }
    }

    @Test
    fun divideAndRemainderTest() {
        assertEquals(Pair(BigInteger.ONE, 1 over 6), (1 over 2).divideAndRemainder(1 over 3))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder(10))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder(10L))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder(BigInteger.valueOf(10L)))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder((10).toShort()))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder((10).toByte()))
        val pair = Rational.NaN.divideAndRemainder(10)
        assertEquals(BigInteger.ZERO, pair.first)
        assertTrue(pair.second.isNaN())
    }

    @Test
    fun ceilTest() {
        val pi = Math.PI.toRational()
        assertEquals(4 over 1, pi.ceil())
        assertEquals(-3 over 1, pi.negate().ceil())
        assertSame(Rational.ONE, Rational.ONE.ceil())
        assertSame(Rational.MINUS_ONE, Rational.MINUS_ONE.ceil())
        assertSame(Rational.NaN, Rational.NaN.ceil())
    }

    @Test
    fun floorTest() {
        val pi = Math.PI.toRational()
        assertEquals(3 over 1, pi.floor())
        assertEquals(-4 over 1, (-pi).floor())
        assertSame(Rational.ONE, Rational.ONE.floor())
        assertSame(Rational.MINUS_ONE, Rational.MINUS_ONE.floor())
        assertSame(Rational.NaN, Rational.NaN.floor())
    }

    @Test
    fun roundTest() {
        val pi = Math.PI.toRational()
        assertEquals(3 over 1, pi.round())
        assertEquals(-3 over 1, (-pi).round())
        assertSame(Rational.ONE, Rational.ONE.round())
        assertSame(Rational.MINUS_ONE, Rational.MINUS_ONE.round())
        assertSame(Rational.ONE, Rational.ONE_HALF.round())
        assertSame(Rational.MINUS_ONE, (-3 over 2).round())
        assertSame(Rational.ZERO, (1 over -3).round())
        assertSame(Rational.MINUS_ONE, (2 over -3).round())
        assertSame(Rational.NaN, Rational.NaN.round())
    }

    @Test
    fun min() {
        assertSame(Rational.MINUS_ONE, min(Rational.ONE, Rational.MINUS_ONE, Rational.ZERO))
        val b = Rational.valueOf(3)
        assertSame(b, min(Rational.valueOf(12), b, Rational.TEN))
        assertSame(Rational.NaN, min(b, Rational.TEN, Rational.NaN, Rational.ZERO))
    }
    @Test
    fun max() {
        assertSame(Rational.ONE, max(Rational.ONE, Rational.MINUS_ONE, Rational.ZERO))
        val b = Rational.valueOf(3)
        assertSame(b, max(Rational.ZERO, Rational.ONE, b))
        assertSame(Rational.NaN, max(b, Rational.TEN, Rational.NaN, Rational.ZERO))
    }

    @Test
    fun toStringBuilderDefaultFormat() {
        val x = -1 over 3
        val b = StringBuilder()
        x.toStringBuilder(b)
        assertEquals("-1/3", b.toString())
    }

    @Test
    fun toStringBuilderMixedFormat() {
        val x = -5 over 3
        val b = x.toStringBuilder(Rational.MIXED)
        assertEquals("-1 2/3", b.toString())
    }

    @Test
    fun toStringBinary() {
        val format = Rational.BINARY
        assertEquals("11", (3 over 1).toString(format))
        assertEquals("1.1", (3 over 2).toString(format))
        assertEquals("-11", (-3 over 1).toString(format))
        assertEquals("-1.1", (-3 over 2).toString(format))
    }

    @Test
    fun toStringOctal() {
        val format = Rational.OCTAL
        assertEquals("11", (9 over 1).toString(format))
        assertEquals("4.4", (9 over 2).toString(format))
        assertEquals("-11", (-9 over 1).toString(format))
        assertEquals("-4.4", (-9 over 2).toString(format))
    }
    @Test
    fun toStringHexadecimal() {
        val format = Rational.HEXADECIMAL
        assertEquals("11", (17 over 1).toString(format))
        assertEquals("4.8", (9 over 2).toString(format))
        assertEquals("-11", (-17 over 1).toString(format))
        assertEquals("-4.8", (-9 over 2).toString(format))
    }
}
