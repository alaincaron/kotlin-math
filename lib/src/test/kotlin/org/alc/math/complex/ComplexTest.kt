package org.alc.math.complex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Math.PI
import kotlin.test.*
import kotlin.test.assertEquals


class ComplexTest {

    @Test
    fun testToComplexSpecial() {
        assertEquals(Complex.INF, "Infinity".toComplex())
        assertEquals(Complex.NaN, "NaN".toComplex())
        assertThrows<NumberFormatException> { "".toComplex() }
        assertThrows<NumberFormatException> { "foobar".toComplex() }
        assertThrows<NumberFormatException> { "3+4j".toComplex() }
        assertThrows<NumberFormatException> { "+-3".toComplex() }
        assertThrows<NumberFormatException> { "2+-3i".toComplex() }
        assertThrows<NumberFormatException> { "2+3i-3-5i".toComplex() }
    }

    @Test
    fun testIBuilder() {
        assertEquals(2.0 + 3.0.I, Complex.create(2.0, 3.0))
        assertEquals(2.0 + 3.0 * Complex.I, Complex.create(2.0, 3.0))
    }


    @Test
    fun testPolar() {
        assertQuasiEquals(Complex.I, exp((PI / 2).I))
    }

    @Test
    fun testEulerEquation() {
        val z = "2.0+3.0I".toComplex()
        assertQuasiEquals(cos(z) + Complex.I * sin(z), exp(Complex.I * z))
    }

    @Test
    fun testMod() {
        assertEquals(Double.POSITIVE_INFINITY, Complex.INF.mod)
        assertEquals(Double.NaN, Complex.NaN.mod)
        val z = "2+3i".toComplex()
        val r = z.mod
        assertQuasiEquals((!z * z).re, r * r)
    }

    @Test
    fun testArg() {
        assertEquals(0.0, Complex.ZERO.arg)
        assertEquals(Double.NaN, Complex.INF.arg)
        assertEquals(Double.NaN, Complex.NaN.arg)
        assertEquals(0.0, (5 * Complex.ONE).arg)
        assertEquals(0.5, exp(0.5.I).arg)
        assertEquals(PI / 2, 3.I.arg)
        assertQuasiEquals(3, exp(3.I).arg)
        assertEquals(PI, (-2.R).arg)
        assertEquals(4 - 2 * PI, exp(4.I).arg)
        assertEquals(-PI / 2, -3.I.arg)
        assertQuasiEquals(6 - 2 * PI, exp(6.I).arg)
    }

    @Test
    fun testConstants() {
        val z = "2.0+3.0i".toComplex()
        assertSame(Complex.ZERO, z * Complex.ZERO)
        assertSame(z, z * Complex.ONE)
    }


    @Test
    fun testComplexAddAndSub() {
        val z0 = 2.0 + 3.0.I
        assertSame(z0, Complex.ZERO + z0)
        assertEquals(-z0, Complex.ZERO - z0)
        val testData = listOf(
                Triple(Complex.INF, z0, Complex.INF),
                Triple(Complex.NaN, z0, Complex.NaN),
                Triple(z0, Complex.ZERO, z0),
                Triple(Complex.ZERO, Complex.ZERO, Complex.ZERO),
                Triple(Complex.INF, Complex.ZERO, Complex.INF),
                Triple(Complex.NaN, Complex.ZERO, Complex.NaN),
                Triple(z0, Complex.INF, Complex.INF),
                Triple(Complex.ZERO, Complex.INF, Complex.INF),
                Triple(Complex.INF, Complex.INF, Complex.NaN),
                Triple(Complex.NaN, Complex.INF, Complex.NaN),
                Triple(z0, Complex.NaN, Complex.NaN),
                Triple(Complex.ZERO, Complex.NaN, Complex.NaN),
                Triple(Complex.INF, Complex.NaN, Complex.NaN),
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
        assertEquals(Complex.create(d0, 0.0), Complex.ZERO + d0)
        assertEquals(Complex.create(-d0, 0.0), Complex.ZERO - d0)
        assertEquals(Complex.create(5.0, 3.0), z0 + d0)
        assertEquals(Complex.create(-1.0, 3.0), z0 - d0)

        val testData = listOf(
                Triple(Complex.INF, d0, Complex.INF),
                Triple(Complex.NaN, d0, Complex.NaN),
                Triple(Complex.ZERO, 0.0, Complex.ZERO),
                Triple(Complex.INF, 0.0, Complex.INF),
                Triple(Complex.NaN, 0.0, Complex.NaN),
                Triple(z0, Double.POSITIVE_INFINITY, Complex.INF),
                Triple(Complex.ZERO, Double.POSITIVE_INFINITY, Complex.INF),
                Triple(Complex.INF, Double.POSITIVE_INFINITY, Complex.NaN),
                Triple(Complex.NaN, Double.POSITIVE_INFINITY, Complex.NaN),
                Triple(z0, Double.NEGATIVE_INFINITY, Complex.INF),
                Triple(Complex.ZERO, Double.NEGATIVE_INFINITY, Complex.INF),
                Triple(Complex.INF, Double.NEGATIVE_INFINITY, Complex.NaN),
                Triple(Complex.NaN, Double.NEGATIVE_INFINITY, Complex.NaN),
                Triple(z0, Double.NaN, Complex.NaN),
                Triple(Complex.ZERO, Double.NaN, Complex.NaN),
                Triple(Complex.INF, Double.NaN, Complex.NaN),
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
                Triple(Complex.INF, z0, Complex.INF),
                Triple(Complex.NaN, z0, Complex.NaN),
                Triple(z0, Complex.ZERO, Complex.ZERO),
                Triple(Complex.ZERO, Complex.ZERO, Complex.ZERO),
                Triple(Complex.INF, Complex.ZERO, Complex.NaN),
                Triple(Complex.NaN, Complex.ZERO, Complex.NaN),
                Triple(z0, Complex.INF, Complex.INF),
                Triple(Complex.ZERO, Complex.INF, Complex.NaN),
                Triple(Complex.INF, Complex.INF, Complex.INF),
                Triple(Complex.NaN, Complex.INF, Complex.NaN),
                Triple(z0, Complex.NaN, Complex.NaN),
                Triple(Complex.ZERO, Complex.NaN, Complex.NaN),
                Triple(Complex.INF, Complex.NaN, Complex.NaN),
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
        val w0 = Complex.create(9.0, 9.0)
        assertEquals(w0, z0 * d0)
        assertEquals(w0, d0 * z0)
        val testData = listOf(
                Triple(Complex.ZERO, d0, Complex.ZERO),
                Triple(Complex.INF, d0, Complex.INF),
                Triple(Complex.NaN, d0, Complex.NaN),
                Triple(z0, 0.0, Complex.ZERO),
                Triple(Complex.ZERO, 0.0, Complex.ZERO),
                Triple(Complex.INF, 0.0, Complex.NaN),
                Triple(Complex.NaN, 0.0, Complex.NaN),
                Triple(z0, Double.POSITIVE_INFINITY, Complex.INF),
                Triple(Complex.ZERO, Double.POSITIVE_INFINITY, Complex.NaN),
                Triple(Complex.INF, Double.POSITIVE_INFINITY, Complex.INF),
                Triple(Complex.NaN, Double.POSITIVE_INFINITY, Complex.NaN),
                Triple(z0, Double.NEGATIVE_INFINITY, Complex.INF),
                Triple(Complex.ZERO, Double.NEGATIVE_INFINITY, Complex.NaN),
                Triple(Complex.INF, Double.NEGATIVE_INFINITY, Complex.INF),
                Triple(Complex.NaN, Double.NEGATIVE_INFINITY, Complex.NaN),
                Triple(z0, Double.NaN, Complex.NaN),
                Triple(Complex.ZERO, Double.NaN, Complex.NaN),
                Triple(Complex.INF, Double.NaN, Complex.NaN),
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
                Triple(Complex.INF, z0, Complex.INF),
                Triple(Complex.NaN, z0, Complex.NaN),
                Triple(z0, Complex.ZERO, Complex.INF),
                Triple(Complex.ZERO, Complex.ZERO, Complex.NaN),
                Triple(Complex.INF, Complex.ZERO, Complex.INF),
                Triple(Complex.NaN, Complex.ZERO, Complex.NaN),
                Triple(z0, Complex.INF, Complex.ZERO),
                Triple(Complex.ZERO, Complex.INF, Complex.ZERO),
                Triple(Complex.INF, Complex.INF, Complex.NaN),
                Triple(Complex.NaN, Complex.INF, Complex.NaN),
                Triple(z0, Complex.NaN, Complex.NaN),
                Triple(Complex.ZERO, Complex.NaN, Complex.NaN),
                Triple(Complex.INF, Complex.NaN, Complex.NaN),
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
        val w0 = Complex.create(1.0, 1.0)
        val testData = listOf(
                Triple(z0, d0, w0),
                Triple(Complex.ZERO, d0, Complex.ZERO),
                Triple(Complex.INF, d0, Complex.INF),
                Triple(Complex.NaN, d0, Complex.NaN),
                Triple(z0, 0.0, Complex.INF),
                Triple(Complex.ZERO, 0.0, Complex.NaN),
                Triple(Complex.INF, 0.0, Complex.INF),
                Triple(Complex.NaN, 0.0, Complex.NaN),
                Triple(z0, Double.POSITIVE_INFINITY, Complex.ZERO),
                Triple(Complex.ZERO, Double.POSITIVE_INFINITY, Complex.ZERO),
                Triple(Complex.INF, Double.POSITIVE_INFINITY, Complex.NaN),
                Triple(Complex.NaN, Double.POSITIVE_INFINITY, Complex.NaN),
                Triple(z0, Double.NEGATIVE_INFINITY, Complex.ZERO),
                Triple(Complex.ZERO, Double.NEGATIVE_INFINITY, Complex.ZERO),
                Triple(Complex.INF, Double.NEGATIVE_INFINITY, Complex.NaN),
                Triple(Complex.NaN, Double.NEGATIVE_INFINITY, Complex.NaN),
                Triple(z0, Double.NaN, Complex.NaN),
                Triple(Complex.ZERO, Double.NaN, Complex.NaN),
                Triple(Complex.INF, Double.NaN, Complex.NaN),
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
        val w0 = Complex.create(0.5, -0.5)
        val testData = listOf(
                Triple(d0, z0, w0),
                Triple(d0, Complex.ZERO, Complex.INF),
                Triple(d0, Complex.INF, Complex.ZERO),
                Triple(d0, Complex.NaN, Complex.NaN),
                Triple(0.0, z0, Complex.ZERO),
                Triple(0.0, Complex.ZERO, Complex.NaN),
                Triple(0.0, Complex.INF, Complex.ZERO),
                Triple(0.0, Complex.NaN, Complex.NaN),
                Triple(Double.POSITIVE_INFINITY, z0, Complex.INF),
                Triple(Double.POSITIVE_INFINITY, Complex.ZERO, Complex.INF),
                Triple(Double.POSITIVE_INFINITY, Complex.INF, Complex.NaN),
                Triple(Double.POSITIVE_INFINITY, Complex.NaN, Complex.NaN),
                Triple(Double.NEGATIVE_INFINITY, z0, Complex.INF),
                Triple(Double.NEGATIVE_INFINITY, Complex.ZERO, Complex.INF),
                Triple(Double.POSITIVE_INFINITY, Complex.INF, Complex.NaN),
                Triple(Double.POSITIVE_INFINITY, Complex.NaN, Complex.NaN),
                Triple(Double.NaN, z0, Complex.NaN),
                Triple(Double.NaN, Complex.ZERO, Complex.NaN),
                Triple(Double.NaN, Complex.INF, Complex.NaN),
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
        val input = Complex.create(3.0, 4.0)
        val expected = Complex.create(3.0, -4.0)
        assertEquals(expected, input.conj())
        assertEquals(expected, !input)
        assertEquals(25.0, (input * !input).mod)
    }

    @Test
    fun testToString() {
        assertEquals("0.0", Complex.ZERO.toString())
        assertEquals("0.0", Complex.create(-0.0, -0.0).toString())
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
        val z0 = Complex.create(4, 5)
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
        assertTrue(four.equals("4".toComplex()))
    }

    @Test
    fun testHashCode() {
        val z0 = Complex.create(4, 5)
        val z1 = 4 + 5.I
        val four = 4.R
        val fiveI = 5.I
        assertNotEquals(0, z0.hashCode())
        assertNotEquals(0, four.hashCode())
        assertNotEquals(0, fiveI.hashCode())
        assertEquals(z0.hashCode(), z1.hashCode())
    }

    @Test
    fun testConstructor() {
        val z0 = Complex.create(4, 5)
        val z1 = Complex.create(z0)
        assertSame(z0, z1)
        val z2 = Complex.create("4+5i")
        assertEquals(z0, z2)
    }

}
