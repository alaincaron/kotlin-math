package org.alc.math.rational

import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.*

class RationalTest {
    @Test fun addRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(5 over 6, a + b)
    }
    @Test fun addBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2)
        assertEquals(5 over 2, a + b)
    }
    @Test fun addLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(5 over 2, a + b)
    }
    @Test fun addInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(5 over 2, a + b)
    }
    @Test fun add0() {
        val a = 1 over 2
        assertSame(a, a + 0)
        assertSame(a, a + 0L)
        assertSame(a, a + BigInteger.ZERO)
        assertSame(a, a + Rational.ZERO)
    }
    @Test fun subtractRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(1 over 6, a - b)
    }
    @Test fun subtractBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        assertEquals(-3 over 2, a - b)
    }
    @Test fun subtractLong() {
        val a = 1 over 2
        val b = 2L
        assertEquals(-3 over 2, a - b)
    }
    @Test fun subtractInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(-3 over 2, a - b)
    }
    @Test fun subtract0() {
        val a = 1 over 2
        assertSame(a, a - 0)
        assertSame(a, a - 0L)
        assertSame(a, a - BigInteger.ZERO)
        assertSame(a, a - Rational.ZERO)
    }

    @Test fun multiplyRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(1 over 6, a * b)
    }
    @Test fun multiplyBigInteger() {
        val a = 1 over 2
        val b = BigInteger.valueOf(2L)
        assertSame(Rational.ONE, a * b)
    }
    @Test fun multiplyLong() {
        val a = 3 over 2
        val b = 2L
        assertEquals(Rational.ONE * 3, a * b)
    }
    @Test fun multiplyInt() {
        val a = 1 over 2
        val b = 2
        assertSame(Rational.ONE, a * b)
    }
    @Test fun multiplyBy0() {
        val a = 1.toByte() over 2.toByte()
        assertSame(Rational.ZERO, a * 0)
        assertSame(Rational.ZERO, a * 0L)
        assertSame(Rational.ZERO, a * BigInteger.ZERO)
        assertSame(Rational.ZERO, a * Rational.ZERO)
    }

    @Test fun multiplyBy1() {
        val a = 1.toShort() over 2.toShort()
        assertSame(a, a * 1)
        assertSame(a, a * 1L)
        assertSame(a, a * BigInteger.ONE)
        assertSame(a, a * Rational.ONE)
    }

    @Test fun divideRational() {
        val a = 1 over 2
        val b = 1 over 3
        assertEquals(3 over 2, a / b)
    }
    @Test fun divideBigInteger() {
        val a = Rational.valueOf(2)
        val b = BigInteger.valueOf(2L)
        assertSame(Rational.ONE, a / b)
    }
    @Test fun divideLong() {
        val a = 3 over 2
        val b = 2L
        assertEquals(3 over 4, a / b)
    }
    @Test fun divideInt() {
        val a = 1 over 2
        val b = 2
        assertEquals(1 over 4, a / b)
    }
    @Test fun divideBy0() {
        val a = 1 over 2
        assertThrows<ArithmeticException> { a / 0 }
        assertThrows<ArithmeticException> { a / 0L }
        assertThrows<ArithmeticException> { a / BigInteger.ZERO }
        assertThrows<ArithmeticException> { a / Rational.ZERO }
    }

    @Test fun divideBy1() {
        val a = 1 over 2
        assertSame(a, a / 1)
        assertSame(a, a / 1L)
        assertSame(a, a / BigInteger.ONE)
        assertSame(a, a / Rational.ONE)
    }

    @Test fun compareTo() {
        val a = -1 over 2
        val b = -1 over 3
        val c = -b
        val d = -a
        assertTrue( a < b)
        assertTrue( b > a)
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

    @Test fun remainder() {
        val a = 1L over 2L
        val b = BigInteger.valueOf(1) over BigInteger.valueOf(3)
        val c = BigDecimal.valueOf(1) over BigDecimal.valueOf(4)
        assertEquals(1 over 6, a % b)
        assertSame(Rational.ZERO, a % c)
        assertEquals(Rational.ONE, Rational.valueOf(5) % Rational.valueOf(2))
        assertEquals(Rational.ONE, Rational.valueOf(5) % 2)
        assertEquals(Rational.ONE, Rational.valueOf(5) % 2L)
        assertSame(Rational.ONE, Rational.valueOf(5) % BigInteger.valueOf(2L))
    }

    @Test fun power() {
        val a = 2 over 3
        assertSame(Rational.ONE, a.pow(0))
        assertEquals(4 over 9, a.pow(2))
        assertEquals(3 over 2, a.pow(-1))
    }

    @Test fun abs() {
        assertSame(Rational.ZERO, Rational.ZERO.abs())
        assertSame(Rational.ONE, Rational.ONE.abs())
        val a = 1 over 2
        assertSame(a, a.abs())
        val b = -a
        assertSame(Rational.ZERO, a + b)
        assertEquals(a, b.abs())
    }

    @Test fun toRational() {

    }
}
