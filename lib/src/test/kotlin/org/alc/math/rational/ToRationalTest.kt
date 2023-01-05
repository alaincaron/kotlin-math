package org.alc.math.rational

import java.math.BigDecimal
import java.math.BigInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ToRationalTest {
    @Test
    fun testNumber() {
        val a: Number = 0.5
        assertEquals(1 over 2, a.toRational())
    }

    @Test
    fun testBigInteger() {
        val a: Number = BigInteger.valueOf(10)
        assertSame(Rational.TEN, a.toRational())
    }

    @Test
    fun testLong() {
        val a: Number = 10L
        assertSame(Rational.TEN, a.toRational())
    }

    @Test
    fun testInt() {
        val a: Number = 10
        assertSame(Rational.TEN, a.toRational())
    }

    @Test
    fun testShort() {
        val a: Number = (10).toShort()
        assertSame(Rational.TEN, a.toRational())
    }

    @Test
    fun testByte() {
        val a: Number = (10).toByte()
        assertSame(Rational.TEN, a.toRational())
    }

    @Test
    fun testFloat() {
        val a: Number = 0.5f
        assertEquals(1 over 2, a.toRational())
    }

    @Test
    fun testDouble() {
        val a: Number = 0.5
        assertEquals(1 over 2, a.toRational())
    }

    @Test
    fun testBigDecimal() {
        val a: Number = BigDecimal.valueOf(0.5)
        assertEquals(1 over 2, a.toRational())
        assertEquals(1 over 2, a.toRational())
    }
}

