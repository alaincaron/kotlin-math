package org.alc.math.complex

import org.junit.jupiter.api.Test
import java.lang.Math.PI
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertSame


class ComplexTest {

    @Test
    fun testIBuilder() {
        assertEquals(2.0 + 3.0.I, Complex(2.0, 3.0))
        assertEquals(2.0 + 3.0 * Complex.I, Complex(2.0, 3.0))
    }


    @Test
    fun testPolar() {
        assertQuasiEquals(Complex.I, exp((PI / 2).I))
    }

    @Test
    fun testEulerEquation() {
        val z = Complex(2, 3)
        assertQuasiEquals(cos(z) + Complex.I * sin(z), exp(Complex.I * z))
    }

    @Test
    fun invert() {
        assertSame(Complex.ONE, Complex.ONE.invert())
        assertEquals(Complex.I.unaryMinus(), Complex.I.invert())
    }

    @Test
    fun testMod() {
        assertEquals(Double.POSITIVE_INFINITY, Complex.INFINITY.mod)
        assertEquals(Double.NaN, Complex.NaN.mod)
        val z = Complex(2, 3)
        val r = z.mod
        assertQuasiEquals((!z * z).re, r * r)
    }

    @Test
    fun testArg() {
        assertEquals(0.0, Complex.ZERO.arg)
        assertEquals(Double.NaN, Complex.INFINITY.arg)
        assertEquals(Double.NaN, Complex.NaN.arg)
        assertEquals(0.0, (5 * Complex.ONE).arg)
        assertEquals(0.5, exp(0.5.I).arg)
        assertEquals(PI / 2, 3.I.arg)
        assertQuasiEquals(3.0, exp(3.I).arg)
        assertEquals(PI, ((-2).R).arg)
        assertQuasiEquals(4 - 2 * PI, exp(4.I).arg)
        assertEquals(-PI / 2, -3.I.arg)
        assertQuasiEquals(6 - 2 * PI, exp(6.I).arg)
    }

    @Test
    fun testConstants() {
        val z = Complex(2, 3)
        assertSame(Complex.ZERO, z * Complex.ZERO)
        assertSame(z, z * Complex.ONE)
    }


    @Test
    fun testComplexAddAndSub() {
        val z0 = 2.0 + 3.0.I
        assertSame(z0, Complex.ZERO + z0)
        assertEquals(-z0, Complex.ZERO - z0)
        val testData = listOf(
            Triple(Complex.INFINITY, z0, Complex.INFINITY),
            Triple(Complex.NaN, z0, Complex.NaN),
            Triple(z0, Complex.ZERO, z0),
            Triple(Complex.ZERO, Complex.ZERO, Complex.ZERO),
            Triple(Complex.INFINITY, Complex.ZERO, Complex.INFINITY),
            Triple(Complex.NaN, Complex.ZERO, Complex.NaN),
            Triple(z0, Complex.INFINITY, Complex.INFINITY),
            Triple(Complex.ZERO, Complex.INFINITY, Complex.INFINITY),
            Triple(Complex.INFINITY, Complex.INFINITY, Complex.NaN),
            Triple(Complex.NaN, Complex.INFINITY, Complex.NaN),
            Triple(z0, Complex.NaN, Complex.NaN),
            Triple(Complex.ZERO, Complex.NaN, Complex.NaN),
            Triple(Complex.INFINITY, Complex.NaN, Complex.NaN),
            Triple(Complex.NaN, Complex.NaN, Complex.NaN)
        )
        for (t in testData) {
            assertSame(t.third, t.first + t.second, "${t.first} + ${t.second}")
            assertSame(t.third, t.first - t.second, "${t.first} - ${t.second}")
        }
    }

    @Test
    fun testDoubleAddAndSub() {
        val z0 = 2.0 + 3.0.I
        val d0 = 3.0
        assertEquals(Complex(d0, 0.0), Complex.ZERO + d0)
        assertEquals(Complex(-d0, 0.0), Complex.ZERO - d0)
        assertEquals(Complex(5.0, 3.0), z0 + d0)
        assertEquals(Complex(-1.0, 3.0), z0 - d0)

        val testData = listOf(
            Triple(Complex.INFINITY, d0, Complex.INFINITY),
            Triple(Complex.NaN, d0, Complex.NaN),
            Triple(Complex.ZERO, 0.0, Complex.ZERO),
            Triple(Complex.INFINITY, 0.0, Complex.INFINITY),
            Triple(Complex.NaN, 0.0, Complex.NaN),
            Triple(z0, Double.POSITIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.ZERO, Double.POSITIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.INFINITY, Double.POSITIVE_INFINITY, Complex.NaN),
            Triple(Complex.NaN, Double.POSITIVE_INFINITY, Complex.NaN),
            Triple(z0, Double.NEGATIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.ZERO, Double.NEGATIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.INFINITY, Double.NEGATIVE_INFINITY, Complex.NaN),
            Triple(Complex.NaN, Double.NEGATIVE_INFINITY, Complex.NaN),
            Triple(z0, Double.NaN, Complex.NaN),
            Triple(Complex.ZERO, Double.NaN, Complex.NaN),
            Triple(Complex.INFINITY, Double.NaN, Complex.NaN),
            Triple(Complex.NaN, Double.NaN, Complex.NaN)
        )
        for (t in testData) {
            assertSame(t.third, t.first + t.second, "${t.first} + ${t.second}")
            assertSame(t.third, t.first - t.second, "${t.first} - ${t.second}")
            assertSame(t.third, t.second + t.first, "${t.second} + ${t.first}")
            assertSame(-t.third, t.second - t.first, "${t.second} - ${t.first}")
        }
    }


    @Test
    fun testComplexMultiplication() {
        val z0 = 2.0 + 3.0.I
        val testData = listOf(
            Triple(Complex.ZERO, z0, Complex.ZERO),
            Triple(Complex.INFINITY, z0, Complex.INFINITY),
            Triple(Complex.NaN, z0, Complex.NaN),
            Triple(z0, Complex.ZERO, Complex.ZERO),
            Triple(Complex.ZERO, Complex.ZERO, Complex.ZERO),
            Triple(Complex.INFINITY, Complex.ZERO, Complex.NaN),
            Triple(Complex.NaN, Complex.ZERO, Complex.NaN),
            Triple(z0, Complex.INFINITY, Complex.INFINITY),
            Triple(Complex.ZERO, Complex.INFINITY, Complex.NaN),
            Triple(Complex.INFINITY, Complex.INFINITY, Complex.INFINITY),
            Triple(Complex.NaN, Complex.INFINITY, Complex.NaN),
            Triple(z0, Complex.NaN, Complex.NaN),
            Triple(Complex.ZERO, Complex.NaN, Complex.NaN),
            Triple(Complex.INFINITY, Complex.NaN, Complex.NaN),
            Triple(Complex.NaN, Complex.NaN, Complex.NaN)
        )
        for (t in testData) {
            assertSame(t.third, t.first * t.second, "${t.first} * ${t.second}")
        }
    }

    @Test
    fun testDoubleMultiplication() {
        val z0 = 3.0 + 3.0.I
        val d0 = 3.0
        val w0 = Complex(9.0, 9.0)
        assertEquals(w0, z0 * d0)
        assertEquals(w0, d0 * z0)
        val testData = listOf(
            Triple(Complex.ZERO, d0, Complex.ZERO),
            Triple(Complex.INFINITY, d0, Complex.INFINITY),
            Triple(Complex.NaN, d0, Complex.NaN),
            Triple(z0, 0.0, Complex.ZERO),
            Triple(Complex.ZERO, 0.0, Complex.ZERO),
            Triple(Complex.INFINITY, 0.0, Complex.NaN),
            Triple(Complex.NaN, 0.0, Complex.NaN),
            Triple(z0, Double.POSITIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.ZERO, Double.POSITIVE_INFINITY, Complex.NaN),
            Triple(Complex.INFINITY, Double.POSITIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.NaN, Double.POSITIVE_INFINITY, Complex.NaN),
            Triple(z0, Double.NEGATIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.ZERO, Double.NEGATIVE_INFINITY, Complex.NaN),
            Triple(Complex.INFINITY, Double.NEGATIVE_INFINITY, Complex.INFINITY),
            Triple(Complex.NaN, Double.NEGATIVE_INFINITY, Complex.NaN),
            Triple(z0, Double.NaN, Complex.NaN),
            Triple(Complex.ZERO, Double.NaN, Complex.NaN),
            Triple(Complex.INFINITY, Double.NaN, Complex.NaN),
            Triple(Complex.NaN, Double.NaN, Complex.NaN)
        )
        for (t in testData) {
            assertSame(t.third, t.first * t.second, "${t.first} * ${t.second}")
            assertSame(t.third, t.second * t.first, "${t.second} + ${t.first}")
        }
    }

    @Test
    fun testComplexDivision() {
        val z0 = 2.0 + 3.0.I
        val testData = listOf(
            Triple(z0, z0, Complex.ONE),
            Triple(Complex.ZERO, z0, Complex.ZERO),
            Triple(Complex.INFINITY, z0, Complex.INFINITY),
            Triple(Complex.NaN, z0, Complex.NaN),
            Triple(z0, Complex.ZERO, Complex.INFINITY),
            Triple(Complex.ZERO, Complex.ZERO, Complex.NaN),
            Triple(Complex.INFINITY, Complex.ZERO, Complex.INFINITY),
            Triple(Complex.NaN, Complex.ZERO, Complex.NaN),
            Triple(z0, Complex.INFINITY, Complex.ZERO),
            Triple(Complex.ZERO, Complex.INFINITY, Complex.ZERO),
            Triple(Complex.INFINITY, Complex.INFINITY, Complex.NaN),
            Triple(Complex.NaN, Complex.INFINITY, Complex.NaN),
            Triple(z0, Complex.NaN, Complex.NaN),
            Triple(Complex.ZERO, Complex.NaN, Complex.NaN),
            Triple(Complex.INFINITY, Complex.NaN, Complex.NaN),
            Triple(Complex.NaN, Complex.NaN, Complex.NaN)
        )
        for (t in testData) {
            assertSame(t.third, t.first / t.second, "${t.first} / ${t.second}")
        }
    }

    @Test
    fun testDoubleDivision() {
        val z0 = 3.0 + 3.0.I
        val d0 = 3.0
        val w0 = Complex(1.0, 1.0)
        val testData = listOf(
            Triple(z0, d0, w0),
            Triple(Complex.ZERO, d0, Complex.ZERO),
            Triple(Complex.INFINITY, d0, Complex.INFINITY),
            Triple(Complex.NaN, d0, Complex.NaN),
            Triple(z0, 0.0, Complex.INFINITY),
            Triple(Complex.ZERO, 0.0, Complex.NaN),
            Triple(Complex.INFINITY, 0.0, Complex.INFINITY),
            Triple(Complex.NaN, 0.0, Complex.NaN),
            Triple(z0, Double.POSITIVE_INFINITY, Complex.ZERO),
            Triple(Complex.ZERO, Double.POSITIVE_INFINITY, Complex.ZERO),
            Triple(Complex.INFINITY, Double.POSITIVE_INFINITY, Complex.NaN),
            Triple(Complex.NaN, Double.POSITIVE_INFINITY, Complex.NaN),
            Triple(z0, Double.NEGATIVE_INFINITY, Complex.ZERO),
            Triple(Complex.ZERO, Double.NEGATIVE_INFINITY, Complex.ZERO),
            Triple(Complex.INFINITY, Double.NEGATIVE_INFINITY, Complex.NaN),
            Triple(Complex.NaN, Double.NEGATIVE_INFINITY, Complex.NaN),
            Triple(z0, Double.NaN, Complex.NaN),
            Triple(Complex.ZERO, Double.NaN, Complex.NaN),
            Triple(Complex.INFINITY, Double.NaN, Complex.NaN),
            Triple(Complex.NaN, Double.NaN, Complex.NaN)
        )
        for (t in testData) {
            assertEquals(t.third, t.first / t.second, "${t.first} / ${t.second}")
        }
    }

    @Test
    fun testDoubleDivisionReverse() {
        val z0 = 3.0 + 3.0.I
        val d0 = 3.0
        val w0 = Complex(0.5, -0.5)
        val testData = listOf(
            Triple(d0, z0, w0),
            Triple(d0, Complex.ZERO, Complex.INFINITY),
            Triple(d0, Complex.INFINITY, Complex.ZERO),
            Triple(d0, Complex.NaN, Complex.NaN),
            Triple(0.0, z0, Complex.ZERO),
            Triple(0.0, Complex.ZERO, Complex.NaN),
            Triple(0.0, Complex.INFINITY, Complex.ZERO),
            Triple(0.0, Complex.NaN, Complex.NaN),
            Triple(Double.POSITIVE_INFINITY, z0, Complex.INFINITY),
            Triple(Double.POSITIVE_INFINITY, Complex.ZERO, Complex.INFINITY),
            Triple(Double.POSITIVE_INFINITY, Complex.INFINITY, Complex.NaN),
            Triple(Double.POSITIVE_INFINITY, Complex.NaN, Complex.NaN),
            Triple(Double.NEGATIVE_INFINITY, z0, Complex.INFINITY),
            Triple(Double.NEGATIVE_INFINITY, Complex.ZERO, Complex.INFINITY),
            Triple(Double.POSITIVE_INFINITY, Complex.INFINITY, Complex.NaN),
            Triple(Double.POSITIVE_INFINITY, Complex.NaN, Complex.NaN),
            Triple(Double.NaN, z0, Complex.NaN),
            Triple(Double.NaN, Complex.ZERO, Complex.NaN),
            Triple(Double.NaN, Complex.INFINITY, Complex.NaN),
            Triple(Double.NaN, Complex.NaN, Complex.NaN)
        )
        for (t in testData) {
            assertEquals(t.third, t.first / t.second, "${t.first} / ${t.second}")
        }
    }


    @Test
    fun testNumberAddSubMulAndDiv() {
        val z0 = 2 + 4.I
        assertEquals(4.I, z0 + (-2))
        assertEquals(4.I, z0 - 2)
        assertEquals(4 + 8.I, z0 * 2)
        assertEquals(1 + 2.I, z0 / 2)
    }

    @Test
    fun testConjugate() {
        val input = Complex(3.0, 4.0)
        val expected = Complex(3.0, -4.0)
        assertEquals(expected, input.conj())
        assertEquals(expected, !input)
        assertEquals(25.0, (input * !input).mod)
    }

    @Test
    fun testToString() {
        assertEquals("0.0", Complex.ZERO.toString())
        assertEquals("0.0", Complex(-0.0, -0.0).toString())
        assertEquals("1.0", Complex.ONE.toString())
        assertEquals("-1.0", (-1.0).R.toString())
        assertEquals("i", Complex.I.toString())
        assertEquals("-i", (-1.0).I.toString())
        assertEquals("1.0+i", (1 + 1.I).toString())
        assertEquals("1.0-i", (1 - 1.I).toString())
        assertEquals("3.0+2.0i", (3.0 + 2.I).toString())
        assertEquals("3.0-2.0i", (3.0 - 2.I).toString())
        assertEquals("-3.0+2.0i", (-3.0 + 2.I).toString())
        assertEquals("-3.0-2.0i", (-3.0 - 2.I).toString())
    }

    @Test
    fun testEquals() {
        val z0 = Complex(4, 5)
        val z1 = 4 + 5.I
        val four = 4.R
        val fiveI = 5.I
        assertEquals(z0, z1)
        assertNotEquals(z0, four)
        assertNotEquals(z0, fiveI)
        assertFalse(four.equals(4L))
        assertFalse(four.equals(4))
        assertFalse(four.equals(4.0))
        assertFalse(four.equals(4.0f))
        assertFalse(four.equals(4.toBigDecimal()))
        assertFalse(four.equals(4.toBigInteger()))
        assertFalse(four.equals(""))
        assertEquals(four, Complex(4.0, 0.0))
    }

    @Test
    fun testHashCode() {
        val z0 = Complex(4, 5)
        val z1 = 4 + 5.I
        val four = 4.R
        val fiveI = 5.I
        assertNotEquals(0, z0.hashCode())
        assertNotEquals(0, four.hashCode())
        assertNotEquals(0, fiveI.hashCode())
        assertEquals(z0.hashCode(), z1.hashCode())
    }

    @Test
    fun testToStringBuilder() {
        val b = StringBuilder("v = ")
        assertSame(b, Complex.I.toStringBuilder(b))
        assertEquals("v = i", b.toString())
    }
}
