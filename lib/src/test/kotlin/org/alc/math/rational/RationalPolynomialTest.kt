package org.alc.math.rational

import org.alc.math.Point2d
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RationalPolynomialTest {
    @Test
    fun apply() {
        val p = RationalPolynomial(0, 2, 1)
        assertEquals(3 over 1, p.apply(Rational.ONE))

        val q = RationalPolynomial(1, -1, -2)
        assertEquals(4 over 1, q.apply(3 over 1))
    }

    @Test
    fun degree() {
        val p = RationalPolynomial(0, 2, 1)
        assertEquals(1, p.degree())
        assertEquals(Rational.TWO, p.coefficients[0])

        val q = RationalPolynomial(0, 1, -1, -2)
        assertEquals(2, q.degree())
    }

    @Test
    fun derivative() {
        val p = RationalPolynomial(3, 2, 5, 5)
        assertEquals(3, p.degree())
        val q = p.derivative()
        assertEquals(2, q.degree())
        val q1 = RationalPolynomial(9, 4, 5)
        assertEquals(q1, q)
    }

    @Test
    fun integrate() {
        val p = RationalPolynomial(4,3,2,1)
        val q = p.integrate()
        val q1 = RationalPolynomial(1,1,1,1,0)
        assertEquals(q,q1)
        assertEquals(p,q.derivative())
        assertSame(RationalPolynomial.ZERO, RationalPolynomial.ZERO.integrate())

    }

    @Test
    fun root2() {
        val p = RationalPolynomial(1, 0, -2)
        val expected = sqrt(2.0)
        assertEquals(expected, p.root(epsilon = 1e-10))
    }

    @Test
    fun rootDegree1() {
        val p = RationalPolynomial(1, -2)
        assertEquals(2.0, p.root())
        val p1 = RationalPolynomial(2, 1)
        assertEquals(-0.5, p1.root())
    }

    @Test
    fun golden() {
        val p = RationalPolynomial(1, -1, -1)
        val expected = (1.0 + sqrt(5.0)) / 2.0
        assertEquals(expected, p.root(initialGuess = 1.0, epsilon = 1e-10))
    }

    @Test
    fun fourthRootOf25() {
        val p = RationalPolynomial(1, 0, 0, 0, -25)
        assertEquals(sqrt(5.0), p.root())
    }

    @Test
    fun toStringTest() {
        val p = RationalPolynomial(1.0, -1.0, 3.5, -2.0)
        assertEquals("x^3 - x^2 + 7/2x - 2", p.toString())
        assertEquals("-x^3 + x^2 - 7/2x + 2", (-p).toString())
        assertEquals("0", RationalPolynomial.ZERO.toString())
        assertEquals("1", RationalPolynomial.ONE.toString())
        assertEquals("x^3", RationalPolynomial.CUBE.toString())

    }

    @Test
    fun zero() {
        val p = RationalPolynomial(0)
        assertSame(RationalPolynomial.ZERO, p)
    }

    @Test
    fun add() {
        val p1 = RationalPolynomial(1, 0, -2)
        val p2 = RationalPolynomial(-1, 0, 2)
        val p3 = RationalPolynomial(-2, 0, 4)
        assertSame(RationalPolynomial.ZERO, p1 + p2)
        assertSame(p1, p1 + RationalPolynomial.ZERO)
        assertSame(p2, RationalPolynomial.ZERO + p2)
        assertEquals(p2, p3 + p1)
    }

    @Test
    fun subtract() {
        val p1 = RationalPolynomial(1, 0, -2)
        val p2 = RationalPolynomial(-1, 0, 2)
        val p3 = RationalPolynomial(-2, 0, 4)
        assertSame(RationalPolynomial.ZERO, p1 - p1)
        assertSame(p1, p1 - RationalPolynomial.ZERO)
        assertEquals(-p2, RationalPolynomial.ZERO - p2)
        assertEquals(p1, p2 - p3)
    }

    @Test
    fun unaryPlus() {
        val p1 = RationalPolynomial(1, 0, -2)
        assertSame(p1, +p1)
        assertSame(RationalPolynomial.ZERO, +RationalPolynomial.ZERO)
    }

    @Test
    fun unaryMinus() {
        val p1 = RationalPolynomial(1, 0, -2)
        assertEquals(RationalPolynomial(-1, 0, 2), -p1)
        assertSame(RationalPolynomial.ZERO, p1 + (-p1))
        assertSame(RationalPolynomial.ZERO, -RationalPolynomial.ZERO)
    }

    @Test
    fun negate() {
        val p1 = RationalPolynomial(1, 0, -2)
        assertEquals(RationalPolynomial(-1, 0, 2), p1.negate())
        assertSame(RationalPolynomial.ZERO, p1 + p1.negate())
        assertSame(RationalPolynomial.ZERO, RationalPolynomial.ZERO.negate())
    }

    @Test
    fun multiplyByZero() {
        val p1 = RationalPolynomial(1, 0, -2)
        assertSame(RationalPolynomial.ZERO, p1 * RationalPolynomial.ZERO)
        assertSame(RationalPolynomial.ZERO, RationalPolynomial.ZERO * p1)
    }

    @Test
    fun multiplyByOne() {
        val p1 = RationalPolynomial(1, 0, -2)
        assertSame(p1, p1 * RationalPolynomial.ONE)
    }

    @Test
    fun multiplyZeroDegree() {
        val p1 = RationalPolynomial(1, 0, -2)
        val p2 = RationalPolynomial(2)
        assertEquals(RationalPolynomial(2, 0, -4), p1 * p2)
        assertEquals(RationalPolynomial(2, 0, -4), p2 * p1)

        val p3 = RationalPolynomial(-3)
        assertEquals(RationalPolynomial(-6), p2 * p3)
        assertEquals(RationalPolynomial(-6), p3 * p2)
    }

    @Test
    fun multiply() {
        assertEquals(RationalPolynomial.SQUARE, RationalPolynomial.IDENTITY * RationalPolynomial.IDENTITY)
        assertEquals(RationalPolynomial.CUBE, RationalPolynomial.IDENTITY * RationalPolynomial.SQUARE)

        // (x+1) * (x - 1) = x^2 - 1
        val p1 = RationalPolynomial(1, 1)
        val q = RationalPolynomial(1, -1)
        val pq = RationalPolynomial(1, 0, -1)
        assertEquals(pq, p1 * q)
        assertEquals(pq, q * p1)

        // (x+1)^2 = x^2 + 2x + 1
        val p2 = RationalPolynomial(1, 2, 1)
        assertEquals(p2, p1 * p1)

        // (x + 1)^3 = x^3 + 3x^2 + 3x + 1
        val p3 = RationalPolynomial(1, 3, 3, 1)
        assertEquals(p3, p1 * p2)
        assertEquals(p3, p2 * p1)

        // (x + 1)^4 = x^4 + 4x^3 + 6x^2 + 4x + 1
        val p4 = RationalPolynomial(1, 4, 6, 4, 1)
        assertEquals(p4, p1 * p3)
        assertEquals(p4, p2 * p2)
        assertEquals(p4, p3 * p1)
    }

    @Test
    fun divideAndRemainder1() {
        val num = RationalPolynomial(2, 1)
        val den = RationalPolynomial(1, 2)
        val pair = num.divideAndRemainder(den)
        assertEquals(RationalPolynomial(2), pair.first)
        assertEquals(RationalPolynomial(-3), pair.second)
    }

    @Test
    fun divideAndRemainder2() {
        val num = RationalPolynomial(1, 0, -1)
        val den = RationalPolynomial(1, -1)
        val pair = num.divideAndRemainder(den)
        assertEquals(RationalPolynomial(1, 1), pair.first)
        assertEquals(RationalPolynomial.ZERO, pair.second)
    }

    @Test
    fun divideAndRemainder3() {
        val num = RationalPolynomial.CUBE * RationalPolynomial(0.5)
        val den = RationalPolynomial.CUBE
        val pair = num.divideAndRemainder(den)
        assertEquals(RationalPolynomial(0.5), pair.first)
        assertEquals(RationalPolynomial.ZERO, pair.second)
    }

    @Test
    fun minus0() {
        val p1 = RationalPolynomial(-0.0)
        assertSame(RationalPolynomial.ZERO, p1)
        val p2 = RationalPolynomial(1.0, -0.0)
        assertSame(RationalPolynomial.IDENTITY, p2)
        val p3 = RationalPolynomial(1.0, -0.0, -0.0)
        assertSame(RationalPolynomial.SQUARE, p3)
    }

    @Test
    fun linearInterpolation() {
        val p = RationalPolynomial.interpolate(
            Point2d(1 over 1, 5 over 1),
            Point2d(6 over 1, 15 over 1)
        )
        val expected = RationalPolynomial(2 over 1, 3 over 1)
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
        val expected = RationalPolynomial(1, -1, -1)
        assertEquals(expected, p)
        val root = (1.0 + sqrt(5.0)) / 2.0
        assertEquals(root, p.root(initialGuess = 1.0, epsilon = 1e-10))
        assertEquals(0.0, p.apply(root.toRational()).toDouble(), 1e-15)
    }

}
