package org.alc.math.rational

import org.junit.jupiter.api.assertThrows
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
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY + Rational.ONE)
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY + Rational.ONE)
    }

    @Test
    fun isZero() {
        assertTrue(Rational.ZERO.isZero())
        assertFalse(Rational.ONE.isZero())
        assertFalse(Rational.NaN.isZero())
        assertFalse(Rational.POSITIVE_INFINITY.isZero())
        assertFalse(Rational.NEGATIVE_INFINITY.isZero())
    }

    @Test
    fun isPositive() {
        assertTrue(Rational.ONE.isPositive())
        assertFalse(Rational.MINUS_ONE.isPositive())
        assertFalse(Rational.ZERO.isPositive())
        assertFalse(Rational.NaN.isPositive())
        assertTrue(Rational.POSITIVE_INFINITY.isPositive())
        assertFalse(Rational.NEGATIVE_INFINITY.isPositive())
    }

    @Test
    fun isNegative() {
        assertTrue(Rational.MINUS_ONE.isNegative())
        assertFalse(Rational.ONE.isNegative())
        assertFalse(Rational.ZERO.isNegative())
        assertFalse(Rational.NaN.isNegative())
        assertFalse(Rational.POSITIVE_INFINITY.isNegative())
        assertTrue(Rational.NEGATIVE_INFINITY.isNegative())
    }

    @Test
    fun isNaN() {
        assertFalse(Rational.ZERO.isNaN())
        assertTrue(Rational.NaN.isNaN())
        assertFalse(Rational.NEGATIVE_INFINITY.isNaN())
        assertFalse(Rational.POSITIVE_INFINITY.isNaN())
    }

    @Test
    fun isInteger() {
        assertTrue(Rational.MINUS_ONE.isInteger())
        assertTrue(Rational.ONE.isInteger())
        assertTrue(Rational.ZERO.isInteger())
        assertFalse(Rational.ONE_HALF.isInteger())
        assertFalse((-1 over 2).isInteger())
        assertFalse(Rational.NaN.isInteger())
        assertFalse(Rational.NEGATIVE_INFINITY.isInteger())
        assertFalse(Rational.POSITIVE_INFINITY.isInteger())
    }

    @Test
    fun isInfinite() {
        assertFalse(Rational.ONE.isInfinite())
        assertFalse(Rational.ZERO.isInfinite())
        assertFalse(Rational.MINUS_ONE.isInfinite())
        assertFalse(Rational.NaN.isInfinite())
        assertTrue(Rational.NEGATIVE_INFINITY.isInfinite())
        assertTrue(Rational.POSITIVE_INFINITY.isInfinite())
    }

    @Test
    fun reciprocal() {
        assertThrows<ArithmeticException> { Rational.ZERO.reciprocal() }
        assertSame(Rational.ONE, Rational.ONE.reciprocal())
        assertSame(Rational.ONE_HALF, Rational.TWO.reciprocal())
        assertSame(Rational.TWO, Rational.ONE_HALF.reciprocal())
        assertEquals((5 over 2), (2 over 5).reciprocal())
        assertSame(Rational.NaN, Rational.NaN.reciprocal())
        assertSame(Rational.ZERO, Rational.POSITIVE_INFINITY.reciprocal())
        assertSame(Rational.ZERO, Rational.NEGATIVE_INFINITY.reciprocal())
    }

    @Test
    fun addRational() {
        val a = Rational.ONE_HALF
        val b = 1 over 3
        assertEquals(5 over 6, a + b)

        assertSame(Rational.NaN, Rational.NaN + a)
        assertSame(Rational.NaN, a + Rational.NaN)
        assertSame(Rational.NaN, Rational.NaN + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NaN, Rational.NaN + Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NaN, Rational.NaN + Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY + Rational.ONE)
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NaN, Rational.POSITIVE_INFINITY + Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NaN, Rational.POSITIVE_INFINITY + Rational.NaN)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY + Rational.ONE)

        assertSame(Rational.POSITIVE_INFINITY, Rational.ONE + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY + Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NaN, Rational.NEGATIVE_INFINITY + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NaN, Rational.NEGATIVE_INFINITY + Rational.NaN)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.ONE + Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun addBigInteger() {
        val a = Rational.ONE_HALF
        val b = BigInteger.valueOf(2)
        assertEquals(5 over 2, a + b)
        assertSame(Rational.NaN, Rational.NaN + b)
        assertSame(Rational.NaN, b + Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY + b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY + b)

        assertSame(Rational.POSITIVE_INFINITY, b + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, b + Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun addLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(5 over 2, a + b)
        assertSame(Rational.NaN, Rational.NaN + b)
        assertSame(Rational.NaN, b + Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY + b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY + b)

        assertSame(Rational.POSITIVE_INFINITY, b + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, b + Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun addInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(5 over 2, a + b)
        assertSame(Rational.NaN, Rational.NaN + b)
        assertSame(Rational.NaN, b + Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY + b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY + b)

        assertSame(Rational.POSITIVE_INFINITY, b + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, b + Rational.NEGATIVE_INFINITY)
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

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY + 0)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY + 0)

        assertSame(Rational.POSITIVE_INFINITY, 0 + Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, 0 + Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun subtractRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(1 over 6, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY - b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY - b)

        assertSame(Rational.NEGATIVE_INFINITY, b - Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, b - Rational.NEGATIVE_INFINITY)

        assertSame(Rational.NaN, Rational.POSITIVE_INFINITY - Rational.POSITIVE_INFINITY)
        assertSame(Rational.NaN, Rational.NEGATIVE_INFINITY - Rational.NEGATIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY - Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun subtractBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        assertEquals(-3 over 2, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY - b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY - b)

        assertSame(Rational.NEGATIVE_INFINITY, b - Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, b - Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun subtractLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(-3 over 2, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY - b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY - b)

        assertSame(Rational.NEGATIVE_INFINITY, b - Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, b - Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun subtractInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(-3 over 2, a - b)
        assertSame(Rational.NaN, Rational.NaN - b)
        assertSame(Rational.NaN, b - Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY - b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY - b)

        assertSame(Rational.NEGATIVE_INFINITY, b - Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, b - Rational.NEGATIVE_INFINITY)
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

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY - 0)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY - 0)

        assertSame(Rational.NEGATIVE_INFINITY, 0 - Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, 0 - Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun multiplyRational() {
        val a = 1 over 2
        val b = 1 over 3
        val c = -b
        assertEquals(1 over 6, a * b)
        assertEquals(-1 over 6, a * c)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY * c)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY * c)

        assertSame(Rational.POSITIVE_INFINITY, b * Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, b * Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, c * Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, c * Rational.NEGATIVE_INFINITY)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY * Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY * Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY * Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun multiplyBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        val c = -b
        assertSame(Rational.ONE, a * b)
        assertSame(Rational.MINUS_ONE, a * c)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY * c)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY * c)

        assertSame(Rational.POSITIVE_INFINITY, b * Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, b * Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, c * Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, c * Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun multiplyLong() {
        val a = 3 over 2
        val b = 2L
        val c = -b
        assertEquals(Rational.ONE * 3, a * b)
        assertEquals(-3 over 1, a * c)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY * c)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY * c)

        assertSame(Rational.POSITIVE_INFINITY, b * Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, b * Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, c * Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, c * Rational.NEGATIVE_INFINITY)

    }

    @Test
    fun multiplyInt() {
        val a = 1 over 2
        val b = 2
        val c = -b
        assertSame(Rational.ONE, a * b)
        assertSame(Rational.MINUS_ONE, a * c)
        assertSame(Rational.NaN, Rational.NaN * b)
        assertSame(Rational.NaN, b * Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY * b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY * c)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY * c)

        assertSame(Rational.POSITIVE_INFINITY, b * Rational.POSITIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, b * Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NEGATIVE_INFINITY, c * Rational.POSITIVE_INFINITY)
        assertSame(Rational.POSITIVE_INFINITY, c * Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun multiplyBy0() {
        val a = 1.toByte() over 2.toByte()
        assertSame(Rational.ZERO, a * 0)
        assertSame(Rational.ZERO, a * 0L)
        assertSame(Rational.ZERO, a * BigInteger.ZERO)
        assertSame(Rational.ZERO, a * Rational.ZERO)
        assertSame(Rational.NaN, Rational.NaN * Rational.ZERO)
        assertSame(Rational.NaN, Rational.ZERO * Rational.NEGATIVE_INFINITY)
        assertSame(Rational.NaN, Rational.ZERO * Rational.POSITIVE_INFINITY)
        assertSame(Rational.NaN, Rational.POSITIVE_INFINITY * Rational.ZERO)
        assertSame(Rational.NaN, Rational.NEGATIVE_INFINITY * Rational.ZERO)
    }

    @Test
    fun multiplyBy1() {
        val a = 1 over 2
        assertSame(a, a * 1)
        assertSame(a, a * 1L)
        assertSame(a, a * BigInteger.ONE)
        assertSame(a, a * Rational.ONE)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY * Rational.ONE)
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY * Rational.ONE)
    }

    @Test
    fun divideRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(3 over 2, a / b)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)
        assertSame(Rational.NaN, Rational.NEGATIVE_INFINITY / Rational.POSITIVE_INFINITY)
        assertSame(Rational.NaN, Rational.POSITIVE_INFINITY / Rational.POSITIVE_INFINITY)
        assertSame(Rational.ZERO, Rational.ONE / Rational.POSITIVE_INFINITY)
        assertSame(Rational.ZERO, Rational.ONE / Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun divideBigInteger() {
        val a = Rational(2)
        val b = BigInteger.valueOf(2L)
        val c = -b
        assertSame(Rational.ONE, a / b)
        assertSame(Rational.MINUS_ONE, a / c)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY / b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY / c)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY / b)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY / c)
    }

    @Test
    fun divideLong() {
        val a = 3 over 2
        val b = 2L
        val c = -b
        assertEquals(3 over 4, a / b)
        assertEquals(-3 over 4, a / c)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)

        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY / b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY / c)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY / b)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY / c)
    }

    @Test
    fun divideInt() {
        val a = 1 over 2
        val b = 2
        val c = -b
        assertEquals(1 over 4, a / b)
        assertEquals(-1 over 4, a / c)
        assertSame(Rational.NaN, Rational.NaN / b)
        assertSame(Rational.NaN, b / Rational.NaN)
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY / b)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.POSITIVE_INFINITY / c)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY / b)
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY / c)
    }

    @Test
    fun divideBy0() {
        val a = 1 over 2
        assertThrows<ArithmeticException> { a / 0 }
        assertThrows<ArithmeticException> { a / 0L }
        assertThrows<ArithmeticException> { a / BigInteger.ZERO }
        assertThrows<ArithmeticException> { a / Rational.ZERO }
        assertThrows<ArithmeticException> { Rational.NaN / Rational.ZERO }
        assertThrows<ArithmeticException> { Rational.NEGATIVE_INFINITY / Rational.ZERO }
        assertThrows<ArithmeticException> { Rational.POSITIVE_INFINITY / Rational.ZERO }
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
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY / Rational.ONE)
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY / Rational.ONE)
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

        assertTrue(Rational.NaN > Rational.POSITIVE_INFINITY)
        assertTrue(Rational.POSITIVE_INFINITY > Rational.NEGATIVE_INFINITY)
        assertTrue(Rational.POSITIVE_INFINITY > Rational.TEN)
        assertTrue(Rational.TEN * -1 > Rational.NEGATIVE_INFINITY)
    }

    @Test
    fun testEquals() {
        assertTrue(Rational(1, 3).equals(1 over 3))
        assertFalse((1 over 2).equals(1 over 3))
        assertFalse(Rational.NaN.equals(Rational.NaN))
        assertFalse(Rational.ONE.equals(Rational.NaN))
        assertFalse(Rational.NaN.equals(Rational.ONE))
        assertTrue(Rational.NEGATIVE_INFINITY.equals(Rational.NEGATIVE_INFINITY))
        assertTrue(Rational.POSITIVE_INFINITY.equals(Rational.POSITIVE_INFINITY))
        assertFalse(Rational.POSITIVE_INFINITY.equals(Rational.NEGATIVE_INFINITY))
        assertFalse(Rational.NEGATIVE_INFINITY.equals(Rational.POSITIVE_INFINITY))
        assertFalse(Rational.NaN.equals(Rational.POSITIVE_INFINITY))
        assertFalse(Rational.POSITIVE_INFINITY.equals(Rational.NaN))
    }

    @Test
    fun remainder() {
        val a = 1L over 2L
        val b = BigInteger.valueOf(1) over BigInteger.valueOf(3)
        val c = 1 over 4
        assertEquals(1 over 6, a % b)
        assertSame(Rational.ZERO, a % c)
        assertEquals(Rational.ONE, Rational(5) % Rational(2))
        assertEquals(2 over 3, (8 over 3) % 2)
        assertEquals(Rational.ONE, Rational(5) % 2L)
        assertSame(Rational.ONE, Rational(5) % BigInteger.valueOf(2L))
        assertSame(Rational.NaN, a % Rational.NaN)
        assertSame(Rational.NaN, Rational.NaN % a)
        assertSame(Rational.NaN, Rational.POSITIVE_INFINITY % a)
        assertSame(Rational.NaN, Rational.NEGATIVE_INFINITY % a)
        assertSame(b, b % Rational.NEGATIVE_INFINITY)
        assertSame(b, b % Rational.POSITIVE_INFINITY)
    }

    @Test
    fun power() {
        val a = 2 over 3
        assertSame(Rational.ONE, a.pow(0))
        assertEquals(4 over 9, a.pow(2))
        assertEquals(3 over 2, a.pow(-1))
        assertSame(Rational.NaN, Rational.NaN.pow(2))
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY.pow(10))
        assertSame(Rational.ZERO, Rational.POSITIVE_INFINITY.pow(-10))
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY.pow(10))
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY.pow(11))
        assertSame(Rational.ZERO, Rational.NEGATIVE_INFINITY.pow(-1))
        assertSame(Rational.ZERO, Rational.NEGATIVE_INFINITY.pow(-2))
        assertSame(Rational.ONE, Rational.POSITIVE_INFINITY.pow(0))
        assertSame(Rational.ONE, Rational.NEGATIVE_INFINITY.pow(0))
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
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY.abs())
        assertSame(Rational.POSITIVE_INFINITY, Rational.NEGATIVE_INFINITY.abs())
    }

    @Test
    fun toStringTest() {
        assertEquals("0", Rational.ZERO.toString())
        assertEquals("10", Rational.TEN.toString())
        assertEquals("NaN", Rational.NaN.toString())
        assertEquals("Infinity", Rational.POSITIVE_INFINITY.toString())
        assertEquals("-Infinity", Rational.NEGATIVE_INFINITY.toString())
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
        assertEquals("Infinity", Rational.POSITIVE_INFINITY.toString(Rational.PRECISION))
        assertEquals("-Infinity", Rational.NEGATIVE_INFINITY.toString(Rational.PRECISION))
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
        assertEquals("Infinity", Rational.POSITIVE_INFINITY.toString(Rational.PERIODIC))
        assertEquals("-Infinity", Rational.NEGATIVE_INFINITY.toString(Rational.PERIODIC))
    }

    @Test
    fun toBigIntegerTest() {
        assertSame(BigInteger.ONE, Rational.ONE.toBigInteger())
        assertSame(BigInteger.TWO, Rational.TWO.toBigInteger())
        assertEquals(BigInteger.ONE, (3 over 2).toBigInteger())
        assertThrows<ArithmeticException> { Rational.NaN.toBigInteger() }
        assertThrows<ArithmeticException> { Rational.POSITIVE_INFINITY.toBigInteger() }
        assertThrows<ArithmeticException> { Rational.NEGATIVE_INFINITY.toBigInteger() }
    }

    @Test
    fun toBigDecimalTest() {
        assertEquals(BigDecimal.valueOf(1.5), (3 over 2).toBigDecimal())
        assertEquals(BigDecimal.valueOf(150, 2), (3 over 2).toBigDecimal(2, RoundingMode.UNNECESSARY))
        assertEquals(BigDecimal.valueOf(2), (3 over 2).toBigDecimal(RoundingMode.UP))
        assertThrows<ArithmeticException> { Rational.NaN.toBigDecimal() }
        assertThrows<ArithmeticException> { Rational.POSITIVE_INFINITY.toBigDecimal() }
        assertThrows<ArithmeticException> { Rational.NEGATIVE_INFINITY.toBigDecimal() }
    }

    @Test
    fun divideAndRemainderTest() {
        assertEquals(Pair(BigInteger.ONE, 1 over 6), (1 over 2).divideAndRemainder(1 over 3))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder(10))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder(10L))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder(BigInteger.valueOf(10L)))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder((10).toShort()))
        assertEquals(Pair(BigInteger.ONE, Rational.ZERO), Rational.TEN.divideAndRemainder((10).toByte()))
        var pair = Rational.NaN.divideAndRemainder(10)
        assertEquals(BigInteger.ZERO, pair.first)
        assertTrue(pair.second.isNaN())

        pair = Rational.POSITIVE_INFINITY.divideAndRemainder(Rational.TEN)
        assertEquals(BigInteger.ZERO, pair.first)
        assertTrue(pair.second.isNaN())

        pair = Rational.NEGATIVE_INFINITY.divideAndRemainder(Rational.TEN)
        assertEquals(BigInteger.ZERO, pair.first)
        assertTrue(pair.second.isNaN())

        pair = Rational.TEN.divideAndRemainder(Rational.POSITIVE_INFINITY)
        assertEquals(BigInteger.ZERO, pair.first)
        assertSame(Rational.TEN, pair.second)

        pair = Rational.TEN.divideAndRemainder(Rational.NEGATIVE_INFINITY)
        assertEquals(BigInteger.ZERO, pair.first)
        assertSame(Rational.TEN, pair.second)
    }

    @Test
    fun ceilTest() {
        val pi = Math.PI.toRational()
        assertEquals(4 over 1, pi.ceil())
        assertEquals(-3 over 1, pi.negate().ceil())
        assertSame(Rational.ONE, Rational.ONE.ceil())
        assertSame(Rational.MINUS_ONE, Rational.MINUS_ONE.ceil())
        assertSame(Rational.NaN, Rational.NaN.ceil())
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY.ceil())
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY.ceil())
    }

    @Test
    fun floorTest() {
        val pi = Math.PI.toRational()
        assertEquals(3 over 1, pi.floor())
        assertEquals(-4 over 1, (-pi).floor())
        assertSame(Rational.ONE, Rational.ONE.floor())
        assertSame(Rational.MINUS_ONE, Rational.MINUS_ONE.floor())
        assertSame(Rational.NaN, Rational.NaN.floor())
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY.floor())
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY.floor())
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
        assertSame(Rational.POSITIVE_INFINITY, Rational.POSITIVE_INFINITY.round())
        assertSame(Rational.NEGATIVE_INFINITY, Rational.NEGATIVE_INFINITY.round())
    }

    @Test
    fun min() {
        assertSame(Rational.MINUS_ONE, min(Rational.ONE, Rational.MINUS_ONE, Rational.ZERO))
        val b = Rational(3)
        assertSame(b, min(Rational(12), b, Rational.TEN))
        assertSame(Rational.NaN, min(b, Rational.TEN, Rational.NaN, Rational.ZERO))
        assertSame(Rational.ONE, min(Rational.ONE, Rational.TEN, Rational.POSITIVE_INFINITY))
        assertSame(Rational.NEGATIVE_INFINITY, min(Rational.ONE, Rational.TEN, Rational.NEGATIVE_INFINITY))
    }

    @Test
    fun max() {
        assertSame(Rational.ONE, max(Rational.ONE, Rational.MINUS_ONE, Rational.ZERO))
        val b = Rational(3)
        assertSame(b, max(Rational.ZERO, Rational.ONE, b))
        assertSame(Rational.NaN, max(b, Rational.TEN, Rational.NaN, Rational.ZERO))
        assertSame(Rational.POSITIVE_INFINITY, max(Rational.ONE, Rational.TEN, Rational.POSITIVE_INFINITY))
        assertSame(Rational.TEN, max(Rational.ONE, Rational.TEN, Rational.NEGATIVE_INFINITY))
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
        assertEquals("11", (9.toShort() over 1.toShort()).toString(format))
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
