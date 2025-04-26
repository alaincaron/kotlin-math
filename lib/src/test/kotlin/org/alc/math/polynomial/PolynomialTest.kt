package org.alc.math.polynomial

import org.alc.math.Point2d
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class PolynomialTest {
    @Test
    fun apply() {
        val p = Polynomial(0, 2, 1)
        assertEquals(3.0, p.apply(1.0))

        val q = Polynomial(1, -1, -2)
        assertEquals(4.0, q.apply(3.0))
    }

    @Test
    fun degree() {
        val p = Polynomial(0, 2, 1)
        assertEquals(1, p.degree())
        assertEquals(2.0, p.coefficients[0])
        assertEquals(1.0, p.coefficients[1])

        val q = Polynomial(0, 1, -1, -2)
        assertEquals(2, q.degree())
    }

    @Test
    fun derivative() {
        val p = Polynomial(3, 2, 5, 5)
        assertEquals(3, p.degree())
        val q = p.derivative()
        assertEquals(2, q.degree())
        val q1 = Polynomial(9, 4, 5)
        assertEquals(q1, q)
    }

    @Test
    fun integrate() {
        val p = Polynomial(4, 3, 2, 1)
        val q = p.integrate()
        val q1 = Polynomial(1, 1, 1, 1, 0)
        assertEquals(q, q1)
        assertEquals(p, q.derivative())
        assertSame(Polynomial.ZERO, Polynomial.ZERO.integrate())
    }

    @Test
    fun root2() {
        val p = Polynomial(1, 0, -2)
        val expected = sqrt(2.0)
        assertEquals(expected, p.root(epsilon = 1e-10))
    }

    @Test
    fun rootDegree1() {
        val p = Polynomial(1, -2)
        assertEquals(2.0, p.root())
        val p1 = Polynomial(2, 1)
        assertEquals(-0.5, p1.root())
    }

    @Test
    fun golden() {
        val p = Polynomial(1, -1, -1)
        val expected = (1.0 + sqrt(5.0)) / 2.0
        assertEquals(expected, p.root(initialGuess = 1.0, epsilon = 1e-10))
    }

    @Test
    fun fourthRootOf25() {
        val p = Polynomial(1, 0, 0, 0, -25)
        assertEquals(sqrt(5.0), p.root())
    }

    @Test
    fun toStringTest() {
        val p = Polynomial(1.0, -1.0, 3.5, -2.0)
        assertEquals("x^3 - x^2 + 3.5x - 2", p.toString())
        assertEquals("-x^3 + x^2 - 3.5x + 2", (-p).toString())
        assertEquals("0", Polynomial.ZERO.toString())
        assertEquals("1", Polynomial.ONE.toString())
        assertEquals("x^3", Polynomial.CUBE.toString())

    }

    @Test
    fun zero() {
        val p = Polynomial(0)
        assertSame(Polynomial.ZERO, p)
        assertEquals(0, p.degree())
    }

    @Test
    fun add() {
        val p1 = Polynomial(1, 0, -2)
        val p2 = Polynomial(-1, 0, 2)
        val p3 = Polynomial(-2, 0, 4)
        assertSame(Polynomial.ZERO, p1 + p2)
        assertSame(p1, p1 + Polynomial.ZERO)
        assertSame(p2, Polynomial.ZERO + p2)
        assertEquals(p2, p3 + p1)
    }

    @Test
    fun subtract() {
        val p1 = Polynomial(1, 0, -2)
        val p2 = Polynomial(-1, 0, 2)
        val p3 = Polynomial(-2, 0, 4)
        assertSame(Polynomial.ZERO, p1 - p1)
        assertSame(p1, p1 - Polynomial.ZERO)
        assertEquals(-p2, Polynomial.ZERO - p2)
        assertEquals(p1, p2 - p3)
    }

    @Test
    fun unaryPlus() {
        val p1 = Polynomial(1, 0, -2)
        assertSame(p1, +p1)
        assertSame(Polynomial.ZERO, +Polynomial.ZERO)
    }

    @Test
    fun unaryMinus() {
        val p1 = Polynomial(1, 0, -2)
        assertEquals(Polynomial(-1, 0, 2), -p1)
        assertSame(Polynomial.ZERO, p1 + (-p1))
        assertSame(Polynomial.ZERO, -Polynomial.ZERO)
    }

    @Test
    fun negate() {
        val p1 = Polynomial(1, 0, -2)
        assertEquals(Polynomial(-1, 0, 2), p1.negate())
        //assertSame(Polynomial.ZERO, p1 + p1.negate())
        //assertSame(Polynomial.ZERO, Polynomial.ZERO.negate())
    }

    @Test
    fun multiplyByZero() {
        val p1 = Polynomial(1, 0, -2)
        assertSame(Polynomial.ZERO, p1 * Polynomial.ZERO)
        assertSame(Polynomial.ZERO, Polynomial.ZERO * p1)
    }

    @Test
    fun multiplyByOne() {
        val p1 = Polynomial(1, 0, -2)
        assertSame(p1, p1 * Polynomial.ONE)
    }

    @Test
    fun multiplyZeroDegree() {
        val p1 = Polynomial(1, 0, -2)
        val p2 = Polynomial(2)
        assertEquals(Polynomial(2, 0, -4), p1 * p2)
        assertEquals(Polynomial(2, 0, -4), p2 * p1)

        val p3 = Polynomial(-3)
        assertEquals(Polynomial(-6), p2 * p3)
        assertEquals(Polynomial(-6), p3 * p2)
    }

    @Test
    fun multiply() {
        assertEquals(Polynomial.SQUARE, Polynomial.IDENTITY * Polynomial.IDENTITY)
        assertEquals(Polynomial.CUBE, Polynomial.IDENTITY * Polynomial.SQUARE)

        // (x+1) * (x - 1) = x^2 - 1
        val p1 = Polynomial(1, 1)
        val q = Polynomial(1, -1)
        val pq = Polynomial(1, 0, -1)
        assertEquals(pq, p1 * q)
        assertEquals(pq, q * p1)

        // (x+1)^2 = x^2 + 2x + 1
        val p2 = Polynomial(1, 2, 1)
        assertEquals(p2, p1 * p1)

        // (x + 1)^3 = x^3 + 3x^2 + 3x + 1
        val p3 = Polynomial(1, 3, 3, 1)
        assertEquals(p3, p1 * p2)
        assertEquals(p3, p2 * p1)

        // (x + 1)^4 = x^4 + 4x^3 + 6x^2 + 4x + 1
        val p4 = Polynomial(1, 4, 6, 4, 1)
        assertEquals(p4, p1 * p3)
        assertEquals(p4, p2 * p2)
        assertEquals(p4, p3 * p1)
    }

    @Test
    fun divideAndRemainder1() {
        val num = Polynomial(2, 1)
        val den = Polynomial(1, 2)
        val pair = num.divideAndRemainder(den)
        assertEquals(Polynomial(2), pair.first)
        assertEquals(Polynomial(-3), pair.second)
    }

    @Test
    fun divideAndRemainder2() {
        val num = Polynomial(1, 0, -1)
        val den = Polynomial(1, -1)
        val pair = num.divideAndRemainder(den)
        assertEquals(Polynomial(1, 1), pair.first)
        assertEquals(Polynomial.ZERO, pair.second)
    }

    @Test
    fun divideAndRemainder3() {
        val num = Polynomial.CUBE * Polynomial(0.5)
        val den = Polynomial.CUBE
        val pair = num.divideAndRemainder(den)
        assertEquals(Polynomial(0.5), pair.first)
        assertEquals(Polynomial.ZERO, pair.second)
    }

    @Test
    fun minus0() {
        val p1 = Polynomial(-0.0)
        assertSame(Polynomial.ZERO, p1)
        val p2 = Polynomial(1.0, -0.0)
        assertSame(Polynomial.IDENTITY, p2)
        val p3 = Polynomial(1.0, -0.0, -0.0)
        assertSame(Polynomial.SQUARE, p3)
    }

    @Test
    fun linearInterpolation() {
        val p = Polynomial.interpolate(Point2d(1.0, 5.0), Point2d(6.0, 15.0))
        val expected = Polynomial(2.0, 3.0)
        assertEquals(expected, p)
    }

    @Test
    fun linearInterpolationSingularity() {
        assertThrows<ArithmeticException> { Polynomial.interpolate(listOf(Point2d(1.0, 5.0), Point2d(1.0, 8.0))) }
    }

    @Test
    fun polynomialInterpolation() {
        val p = Polynomial.interpolate(Point2d(1.0, -1.0), Point2d(2.0, 1.0), Point2d(3.0, 5.0))
        val expected = Polynomial(1, -1, -1)
        assertEquals(expected, p)
        val root = (1.0 + sqrt(5.0)) / 2.0
        assertEquals(root, p.root(initialGuess = 1.0, epsilon = 1e-10))
        assertEquals(0.0, p.apply(root), 1e-15)
    }
}
