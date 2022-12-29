package org.alc.math.polynomial

import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class PolynomialTest {
    @Test
    fun apply() {
        val p = Polynomial.createFrom(0, 2, 1)
        assertEquals(3.0, p.apply(1.0))

        val q = Polynomial.createFrom(1, -1, -1)
        assertEquals(5.0, q.apply(3.0))
    }

    @Test
    fun degree() {
        val p = Polynomial.createFrom(0, 2, 1)
        assertEquals(1, p.degree())

        val q = Polynomial.createFrom(0, 1, -1, -1)
        assertEquals(2, q.degree())
    }

    @Test
    fun derivative() {
        val p = Polynomial.createFrom(3, 2, 5, 5)
        assertEquals(3, p.degree())
        val q = p.derivative()
        assertEquals(2, q.degree())
        val q1 = Polynomial.createFrom(9,4,5)
        assertEquals(q1, q)
    }

    @Test
    fun root2() {
        val p = Polynomial.createFrom(1, 0, -2)
        val expected = sqrt(2.0)
        assertEquals(expected, p.root(epsilon = 1e-10))
    }

    @Test
    fun golden() {
        val p = Polynomial.createFrom(1, -1, -1)
        val expected = (1.0 + sqrt(5.0)) / 2.0
        assertEquals(expected, p.root(initial_guess = 1.0, epsilon = 1e-10))
    }

    @Test
    fun toStringTest() {
        val p = Polynomial.createFrom(1.0, -1.0, 3.5, -2.0)
        assertEquals("x^3 - x^2 + 3.5x - 2", p.toString())
        assertEquals("-x^3 + x^2 - 3.5x + 2", (-p).toString())
        assertEquals("0", Polynomial.ZERO.toString())
        assertEquals("1", Polynomial.ONE.toString())
        assertEquals("x^3", Polynomial.CUBE.toString())

    }

    @Test
    fun zero() {
        val p = Polynomial.createFrom(0)
        assertSame(Polynomial.ZERO, p)
    }

    @Test
    fun add() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        val p2 = Polynomial.createFrom(-1, 0, 2)
        val p3 = Polynomial.createFrom(-2, 0, 4)
        assertSame(Polynomial.ZERO, p1 + p2)
        assertSame(p1, p1 + Polynomial.ZERO)
        assertSame(p2, Polynomial.ZERO + p2)
        assertEquals(p2, p3 + p1)
    }

    @Test
    fun subtract() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        val p2 = Polynomial.createFrom(-1, 0, 2)
        val p3 = Polynomial.createFrom(-2, 0, 4)
        assertSame(Polynomial.ZERO, p1 - p1)
        assertSame(p1, p1 - Polynomial.ZERO)
        assertEquals(-p2, Polynomial.ZERO - p2)
        assertEquals(p1, p2 - p3)
    }

    @Test
    fun unaryPlus() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        assertSame(p1, +p1)
        assertSame(Polynomial.ZERO, +Polynomial.ZERO)
    }

    @Test
    fun unaryMinus() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        assertEquals(Polynomial.createFrom(-1,0,2), -p1)
        assertSame(Polynomial.ZERO, p1 + (-p1))
        assertSame(Polynomial.ZERO, -Polynomial.ZERO)
    }

    @Test
    fun negate() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        assertEquals(Polynomial.createFrom(-1,0,2), p1.negate())
        //assertSame(Polynomial.ZERO, p1 + p1.negate())
        //assertSame(Polynomial.ZERO, Polynomial.ZERO.negate())
    }

    @Test fun multiplyByZero() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        assertSame(Polynomial.ZERO, p1 * Polynomial.ZERO)
        assertSame(Polynomial.ZERO, Polynomial.ZERO * p1)
    }
    @Test
    fun multiplyByOne() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        assertSame(p1, p1 * Polynomial.ONE)
    }

    @Test
    fun multiplyZeroDegree() {
        val p1 = Polynomial.createFrom(1, 0, -2)
        val p2 = Polynomial.createFrom(2)
        assertEquals(Polynomial.createFrom(2,0,-4), p1 * p2)
        assertEquals(Polynomial.createFrom(2,0,-4), p2 * p1)

        val p3 = Polynomial.createFrom(-3)
        assertEquals(Polynomial.createFrom(-6), p2 * p3)
        assertEquals(Polynomial.createFrom(-6), p3 * p2)
    }

    @Test
    fun multiply() {
        assertEquals(Polynomial.SQUARE, Polynomial.IDENTITY * Polynomial.IDENTITY)
        assertEquals(Polynomial.CUBE, Polynomial.IDENTITY * Polynomial.SQUARE)

        // (x+1) * (x - 1) = x^2 - 1
        val p1 = Polynomial.createFrom(1,1)
        val q = Polynomial.createFrom(1,-1)
        val pq = Polynomial.createFrom(1,0,-1)
        assertEquals(pq, p1 * q)
        assertEquals(pq, q * p1)

        // (x+1)^2 = x^2 + 2x + 1
        val p2 = Polynomial.createFrom(1,2,1)
        assertEquals(p2, p1 * p1)

        // (x + 1)^3 = x^3 + 3x^2 + 3x + 1
        val p3 = Polynomial.createFrom(1,3,3,1)
        assertEquals(p3, p1 * p2)
        assertEquals(p3, p2 * p1)

        // (x + 1)^4 = x^4 + 4x^3 + 6x^2 + 4x + 1
        val p4 = Polynomial.createFrom(1,4,6,4,1)
        assertEquals(p4, p1 * p3)
        assertEquals(p4, p2 * p2)
        assertEquals(p4, p3 * p1)
    }

    @Test
    fun divideAndRemainder1() {
        val num = Polynomial.createFrom(2,1)
        val den = Polynomial.createFrom(1, 2)
        val pair = num.divideAndRemainder(den)
        assertEquals(Polynomial.createFrom(2), pair.first)
        assertEquals(Polynomial.createFrom(-3), pair.second)
    }

    @Test
    fun divideAndRemainder2() {
        val num = Polynomial.createFrom(1,0,-1)
        val den = Polynomial.createFrom(1,-1)
        val pair = num.divideAndRemainder(den)
        assertEquals(Polynomial.createFrom(1,1), pair.first)
        assertEquals(Polynomial.ZERO, pair.second)
    }
    @Test
    fun divideAndRemainder3() {
        val num = Polynomial.CUBE * Polynomial.createFrom(0.5)
        val den = Polynomial.CUBE
        val pair = num.divideAndRemainder(den)
        assertEquals(Polynomial.createFrom(0.5), pair.first)
        assertEquals(Polynomial.ZERO, pair.second)
    }

    @Test
    fun minus0() {
        val p1 = Polynomial.createFrom(-0.0)
        assertSame(Polynomial.ZERO, p1)
        val p2 = Polynomial.createFrom(1.0, -0.0)
        assertSame(Polynomial.IDENTITY, p2)
        val p3 = Polynomial.createFrom(1.0, -0.0, -0.0)
        assertSame(Polynomial.SQUARE, p3)
    }
}
