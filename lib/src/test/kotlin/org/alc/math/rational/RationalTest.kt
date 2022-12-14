package org.alc.math.rational

import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class RationalTest {
    @Test
    fun addRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(5 over 6, a + b)
    }

    @Test
    fun addBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2)
        assertEquals(5 over 2, a + b)
    }

    @Test
    fun addLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(5 over 2, a + b)
    }

    @Test
    fun addInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(5 over 2, a + b)
    }

    @Test
    fun add0() {
        val a = 1 over 2
        assertSame(a, a + 0)
        assertSame(a, a + 0L)
        assertSame(a, a + BigInteger.ZERO)
        assertSame(a, a + Rational.ZERO)
    }

    @Test
    fun subtractRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(1 over 6, a - b)
    }

    @Test
    fun subtractBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        assertEquals(-3 over 2, a - b)
    }

    @Test
    fun subtractLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(-3 over 2, a - b)
    }

    @Test
    fun subtractInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(-3 over 2, a - b)
    }

    @Test
    fun subtract0() {
        val a = 1 over 2
        assertSame(a, a - 0)
        assertSame(a, a - 0L)
        assertSame(a, a - BigInteger.ZERO)
        assertSame(a, a - Rational.ZERO)
    }

    @Test
    fun multiplyRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(1 over 6, a * b)
    }

    @Test
    fun multiplyBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        assertSame(Rational.ONE, a * b)
    }

    @Test
    fun multiplyLong() {
        val a = 3 over 2
        val b = 2L
        assertEquals(Rational.ONE * 3, a * b)
    }

    @Test
    fun multiplyInt() {
        val a = 1 over 2
        val b = 2
        assertSame(Rational.ONE, a * b)
    }

    @Test
    fun multiplyBy0() {
        val a = 1.toByte() over 2.toByte()
        assertSame(Rational.ZERO, a * 0)
        assertSame(Rational.ZERO, a * 0L)
        assertSame(Rational.ZERO, a * BigInteger.ZERO)
        assertSame(Rational.ZERO, a * Rational.ZERO)
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
    }

    @Test
    fun divideBigInteger() {
        val a = Rational.valueOf(2)
        val b = BigInteger.valueOf(2L)
        assertSame(Rational.ONE, a / b)
    }

    @Test
    fun divideLong() {
        val a = 3 over 2
        val b = 2L
        assertEquals(3 over 4, a / b)
    }

    @Test
    fun divideInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(1 over 4, a / b)
    }

    @Test
    fun divideBy0() {
        val a = 1 over 2
        assertThrows<ArithmeticException> { a / 0 }
        assertThrows<ArithmeticException> { a / 0L }
        assertThrows<ArithmeticException> { a / BigInteger.ZERO }
        assertThrows<ArithmeticException> { a / Rational.ZERO }
    }

    @Test
    fun divideBy1() {
        val a = 1 over 2
        assertSame(a, a / 1)
        assertSame(a, a / 1L)
        assertSame(a, a / BigInteger.ONE)
        assertSame(a, a / Rational.ONE)
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
    }

    @Test
    fun remainder() {
        val a = 1L over 2L
        val b = BigInteger.valueOf(1) over BigInteger.valueOf(3)
        val c = 1 over 4
        assertEquals(1 over 6, a % b)
        assertSame(Rational.ZERO, a % c)
        assertEquals(Rational.ONE, Rational.valueOf(5) % Rational.valueOf(2))
        assertEquals(Rational.ONE, Rational.valueOf(5) % 2)
        assertEquals(Rational.ONE, Rational.valueOf(5) % 2L)
        assertSame(Rational.ONE, Rational.valueOf(5) % BigInteger.valueOf(2L))
    }

    @Test
    fun power() {
        val a = 2 over 3
        assertSame(Rational.ONE, a.pow(0))
        assertEquals(4 over 9, a.pow(2))
        assertEquals(3 over 2, a.pow(-1))
    }

    @Test
    fun abs() {
        assertSame(Rational.ZERO, Rational.ZERO.abs())
        assertSame(Rational.ONE, Rational.ONE.abs())
        val a = 1 over 2
        assertSame(a, a.abs())
        val b = -a
        assertSame(Rational.ZERO, a + b)
        assertEquals(a, b.abs())
    }

    @Test
    fun signum() {
        assertEquals(0, Rational.ZERO.signum())
        assertEquals(1, Rational.TEN.signum())
        assertEquals(-1, (-1 over 10).signum())
    }

    @Test
    fun toStringTest() {
        assertEquals("0", Rational.ZERO.toString())
        assertEquals("10", Rational.TEN.toString())
    }

    @Test
    fun toStringPrecisionTest() {
        assertEquals("0.3333333333", (1 over 3).toString(Rational.Precision()))
        assertEquals("0.011", (1 over 90).toString(Rational.Precision(3)))
        assertEquals("0", (1 over 5).toString(Rational.Precision(0)))
        assertEquals("0.1428571428", (1 over 7).toString(Rational.Precision()))
        assertEquals("0.5", (1 over 2).toString(Rational.Precision(10)))
        assertEquals("10", Rational.TEN.toString(Rational.Precision()))
        assertEquals(Math.PI.toString(), Math.PI.toRational().toString(Rational.Precision(15)))
    }

    @Test
    fun toStringPeriodTest() {
        assertEquals("0.[3]", (1 over 3).toString(Rational.Period))
        assertEquals("0.0[1]", (1 over 90).toString(Rational.Period))
        assertEquals("0.[142857]", (1 over 7).toString(Rational.Period))
        assertEquals("0.000[142857]", (1 over 7000).toString(Rational.Period))
        assertEquals("0.5", (1 over 2).toString(Rational.Period))
        assertEquals("10", Rational.TEN.toString(Rational.Period))
        assertEquals(Math.PI.toString(), Math.PI.toRational().toString(Rational.Period))
    }

    @Test
    fun toBigIntegerTest() {
        assertSame(BigInteger.ONE, Rational.ONE.toBigInteger())
        assertSame(BigInteger.TWO, Rational.TWO.toBigInteger())
        assertEquals(BigInteger.ONE, (3 over 2).toBigInteger())
    }
    @Test
    fun toBigDecimalTest() {
        assertEquals(BigDecimal.valueOf(1.5), (3 over 2).toBigDecimal())
        assertEquals(BigDecimal.valueOf(150,2), (3 over 2).toBigDecimal(2, RoundingMode.UNNECESSARY))
        assertEquals(BigDecimal.valueOf(2), (3 over 2).toBigDecimal(RoundingMode.UP))
    }
}
