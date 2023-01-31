package org.alc.math.rational

import org.alc.math.Point2d
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class RationalPolynomialTest {
    @Test
    fun apply() {
        val p = RationalPolynomial.create(0, 2, 1)
        assertEquals(3 over 1, p.apply(Rational.ONE))
        assertIs<RationalPolynomial.Linear>(p)

        val q = RationalPolynomial.create(1, -1, -2)
        assertEquals(4 over 1, q.apply(3 over 1))
        assertIs<RationalPolynomial.Quadratic>(q)
    }

    @Test
    fun degree() {
        val p = RationalPolynomial.create(0, 2, 1)
        assertEquals(1, p.degree())
        assertIs<RationalPolynomial.Linear>(p)
        assertEquals(Rational.TWO, p.m)

        val q = RationalPolynomial.create(0, 1, -1, -2)
        assertEquals(2, q.degree())
        assertIs<RationalPolynomial.Quadratic>(q)
        assertEquals(1 over 1, q.a)
        assertEquals(-1 over 1, q.b)
        assertEquals(-2 over 1, q.c)
        assertEquals(1 over 2, q.extremum)
    }

    @Test
    fun derivative() {
        val p = RationalPolynomial.create(3, 2, 5, 5)
        assertEquals(3, p.degree())
        val q = p.derivative()
        assertEquals(2, q.degree())
        assertIs<RationalPolynomial.Quadratic>(q)
        val q1 = RationalPolynomial.create(9, 4, 5)
        assertEquals(q1, q)
    }

    @Test
    fun root2() {
        val p = RationalPolynomial.create(1, 0, -2)
        val expected = sqrt(2.0)
        assertEquals(expected, p.root(epsilon = 1e-10))
    }

    @Test
    fun rootDegree1() {
        val p = RationalPolynomial.create(1, -2)
        assertEquals(2.0, p.root())
        val p1 = RationalPolynomial.create(2, 1)
        assertEquals(-0.5, p1.root())
    }

    @Test
    fun golden() {
        val p = RationalPolynomial.create(1, -1, -1)
        val expected = (1.0 + sqrt(5.0)) / 2.0
        assertEquals(expected, p.root(initial_guess = 1.0, epsilon = 1e-10))
    }

    @Test
    fun fourthRootOf25() {
        val p = RationalPolynomial.create(1, 0, 0, 0, -25)
        assertEquals(sqrt(5.0), p.root())
    }

    @Test
    fun toStringTest() {
        val p = RationalPolynomial.create(1.0, -1.0, 3.5, -2.0)
        assertEquals("x^3 - x^2 + 7/2x - 2", p.toString())
        assertEquals("-x^3 + x^2 - 7/2x + 2", (-p).toString())
        assertEquals("0", RationalPolynomial.ZERO.toString())
        assertEquals("1", RationalPolynomial.ONE.toString())
        assertEquals("x^3", RationalPolynomial.CUBE.toString())

    }

    @Test
    fun zero() {
        val p = RationalPolynomial.create(0)
        assertSame(RationalPolynomial.ZERO, p)
        assertIs<RationalPolynomial.Constant>(p)
    }

    @Test
    fun add() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        val p2 = RationalPolynomial.create(-1, 0, 2)
        val p3 = RationalPolynomial.create(-2, 0, 4)
        assertSame(RationalPolynomial.ZERO, p1 + p2)
        assertSame(p1, p1 + RationalPolynomial.ZERO)
        assertSame(p2, RationalPolynomial.ZERO + p2)
        assertEquals(p2, p3 + p1)
    }

    @Test
    fun subtract() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        val p2 = RationalPolynomial.create(-1, 0, 2)
        val p3 = RationalPolynomial.create(-2, 0, 4)
        assertSame(RationalPolynomial.ZERO, p1 - p1)
        assertSame(p1, p1 - RationalPolynomial.ZERO)
        assertEquals(-p2, RationalPolynomial.ZERO - p2)
        assertEquals(p1, p2 - p3)
    }

    @Test
    fun unaryPlus() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        assertSame(p1, +p1)
        assertSame(RationalPolynomial.ZERO, +RationalPolynomial.ZERO)
    }

    @Test
    fun unaryMinus() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        assertEquals(RationalPolynomial.create(-1, 0, 2), -p1)
        assertSame(RationalPolynomial.ZERO, p1 + (-p1))
        assertSame(RationalPolynomial.ZERO, -RationalPolynomial.ZERO)
    }

    @Test
    fun negate() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        assertEquals(RationalPolynomial.create(-1, 0, 2), p1.negate())
        //assertSame(Polynomial.ZERO, p1 + p1.negate())
        //assertSame(Polynomial.ZERO, Polynomial.ZERO.negate())
    }

    @Test
    fun multiplyByZero() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        assertSame(RationalPolynomial.ZERO, p1 * RationalPolynomial.ZERO)
        assertSame(RationalPolynomial.ZERO, RationalPolynomial.ZERO * p1)
    }

    @Test
    fun multiplyByOne() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        assertSame(p1, p1 * RationalPolynomial.ONE)
    }

    @Test
    fun multiplyZeroDegree() {
        val p1 = RationalPolynomial.create(1, 0, -2)
        val p2 = RationalPolynomial.create(2)
        assertEquals(RationalPolynomial.create(2, 0, -4), p1 * p2)
        assertEquals(RationalPolynomial.create(2, 0, -4), p2 * p1)

        val p3 = RationalPolynomial.create(-3)
        assertEquals(RationalPolynomial.create(-6), p2 * p3)
        assertEquals(RationalPolynomial.create(-6), p3 * p2)
    }

    @Test
    fun multiply() {
        assertEquals(RationalPolynomial.SQUARE, RationalPolynomial.IDENTITY * RationalPolynomial.IDENTITY)
        assertEquals(RationalPolynomial.CUBE, RationalPolynomial.IDENTITY * RationalPolynomial.SQUARE)

        // (x+1) * (x - 1) = x^2 - 1
        val p1 = RationalPolynomial.create(1, 1)
        val q = RationalPolynomial.create(1, -1)
        val pq = RationalPolynomial.create(1, 0, -1)
        assertEquals(pq, p1 * q)
        assertEquals(pq, q * p1)

        // (x+1)^2 = x^2 + 2x + 1
        val p2 = RationalPolynomial.create(1, 2, 1)
        assertEquals(p2, p1 * p1)

        // (x + 1)^3 = x^3 + 3x^2 + 3x + 1
        val p3 = RationalPolynomial.create(1, 3, 3, 1)
        assertEquals(p3, p1 * p2)
        assertEquals(p3, p2 * p1)

        // (x + 1)^4 = x^4 + 4x^3 + 6x^2 + 4x + 1
        val p4 = RationalPolynomial.create(1, 4, 6, 4, 1)
        assertEquals(p4, p1 * p3)
        assertEquals(p4, p2 * p2)
        assertEquals(p4, p3 * p1)
    }

    @Test
    fun divideAndRemainder1() {
        val num = RationalPolynomial.create(2, 1)
        val den = RationalPolynomial.create(1, 2)
        val pair = num.divideAndRemainder(den)
        assertEquals(RationalPolynomial.create(2), pair.first)
        assertEquals(RationalPolynomial.create(-3), pair.second)
    }

    @Test
    fun divideAndRemainder2() {
        val num = RationalPolynomial.create(1, 0, -1)
        val den = RationalPolynomial.create(1, -1)
        val pair = num.divideAndRemainder(den)
        assertEquals(RationalPolynomial.create(1, 1), pair.first)
        assertEquals(RationalPolynomial.ZERO, pair.second)
    }

    @Test
    fun divideAndRemainder3() {
        val num = RationalPolynomial.CUBE * RationalPolynomial.create(0.5)
        val den = RationalPolynomial.CUBE
        val pair = num.divideAndRemainder(den)
        assertEquals(RationalPolynomial.create(0.5), pair.first)
        assertEquals(RationalPolynomial.ZERO, pair.second)
    }

    @Test
    fun minus0() {
        val p1 = RationalPolynomial.create(-0.0)
        assertSame(RationalPolynomial.ZERO, p1)
        val p2 = RationalPolynomial.create(1.0, -0.0)
        assertSame(RationalPolynomial.IDENTITY, p2)
        val p3 = RationalPolynomial.create(1.0, -0.0, -0.0)
        assertSame(RationalPolynomial.SQUARE, p3)
    }

    @Test
    fun linearInterpolation() {
        val p = RationalPolynomial.interpolate(
            Point2d(1 over 1, 5 over 1),
            Point2d(6 over 1, 15 over 1)
        )
        val expected = RationalPolynomial.create(2 over 1, 3 over 1)
        assertEquals(expected, p)
    }

    @Test
    fun linearInterpolationSingularity() {
        assertThrows<ArithmeticException> {
            RationalPolynomial.interpolate(
                listOf(
                    Point2d(1 over 1, 5 over 1),
                    Point2d(1 over 1, 8 over 1)
                )
            )
        }
    }

    @Test
    fun polynomialInterpolation() {
        val p = RationalPolynomial.interpolate(
            Point2d(1 over 1, -1 over 1),
            Point2d(2 over 1, 1 over 1),
            Point2d(3 over 1, 5 over 1)
        )
        val expected = RationalPolynomial.create(1, -1, -1)
        assertEquals(expected, p)
        val root = (1.0 + sqrt(5.0)) / 2.0
        assertEquals(root, p.root(initial_guess = 1.0, epsilon = 1e-10))
        assertEquals(0.0, p.apply(root.toRational()).toDouble(), 1e-15)
    }

}
