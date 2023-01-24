package org.alc.math.complex

import org.junit.jupiter.api.Test
import java.lang.Math.PI
import kotlin.math.E
import kotlin.test.assertEquals

class BasicFunctionsTest {

    @Test
    fun testExp() {
        assertEquals(Complex.ONE, exp(0))
        assertEquals(E.R, exp(1))
        assertQuasiEquals(-Complex.ONE, exp(PI.I))
        assertQuasiEquals(Complex.I, exp(PI.I / 2))
        assertQuasiEquals((E * E).I, exp(2 + PI.I / 2))
        assertQuasiEquals(-Complex.I, exp(3 * PI.I / 2))
        assertEquals(Complex.NaN, exp(Complex.INF))
        assertEquals(Complex.NaN, exp(Complex.NaN))
    }

    @Test
    fun testSin() {
        assertEquals(Complex.ZERO, sin(0))
        assertEquals(Complex.ONE, sin(PI / 2))
        assertQuasiEquals(1.1752011936438014.I, sin(Complex.I))
        assertQuasiEquals((-1.1752011936438014).I, sin((-1).I))
        assertEquals(Complex.NaN, sin(Complex.INF))
        assertEquals(Complex.NaN, sin(Complex.NaN))
    }

    @Test
    fun testCos() {
        assertEquals(Complex.ONE, cos(0))
        assertQuasiEquals(Complex.ZERO, cos(PI / 2))
        assertEquals(-Complex.ONE, cos(PI))
        assertQuasiEquals(1.543080634815244.R, cos(Complex.I))
        assertQuasiEquals(1.543080634815244.R, cos(-Complex.I))
        assertEquals(Complex.NaN, cos(Complex.INF))
        assertEquals(Complex.NaN, cos(Complex.NaN))
    }

    @Test
    fun testLn() {
        assertEquals(Complex.NaN, ln(0))
        assertEquals(Complex.ZERO, ln(1))
        assertEquals(Complex.ONE, ln(E))
        assertEquals(2.R, ln(E * E))
        assertQuasiEquals(PI.I, ln(-1))
        assertQuasiEquals(PI.I / 2, ln(Complex.I))
        assertQuasiEquals(-PI.I / 2, ln(-Complex.I))
        assertEquals(Complex.NaN, ln(0))
        assertEquals(Complex.NaN, ln(Complex.INF))
        assertEquals(Complex.NaN, ln(Complex.NaN))
    }

    @Test
    fun testSqrt() {
        assertEquals(Complex.ZERO, sqrt(0))
        assertEquals(Complex.ONE, sqrt(1))
        assertEquals(2.R, sqrt(4))
        assertEquals(2.I, sqrt(-4))
        assertEquals(3 + 4.I, sqrt(-7 + 24.I))
        assertQuasiEquals(sqrt(0.5) + sqrt(0.5) * Complex.I, sqrt(Complex.I))
        assertQuasiEquals(sqrt(0.5) - sqrt(0.5) * Complex.I, sqrt(-Complex.I))
        assertEquals(Complex.INF, sqrt(Complex.INF))
        assertEquals(Complex.NaN, sqrt(Complex.NaN))
    }

    @Test
    fun testPowDoubleComplex() {
        val w0 = ln(2) + PI.I
        val w1 = 2 * PI.I
        val w2 = 2.R
        val w3 = 0.5 + 14.134725141734695.I

        assertEquals(Complex.ONE, pow(0, Complex.ZERO))
        assertEquals(Complex.ZERO, pow(0, Complex.ONE))
        assertEquals(Complex.ZERO, pow(0, Complex.I))
        assertEquals(Complex.ZERO, pow(0, w0))
        assertEquals(Complex.ZERO, pow(0, w1))
        assertEquals(Complex.ZERO, pow(0, w2))
        assertEquals(Complex.NaN, pow(0, Complex.INF))
        assertEquals(Complex.NaN, pow(0, Complex.NaN))

        assertEquals(Complex.ONE, pow(1, Complex.ZERO))
        assertEquals(Complex.ONE, pow(1, Complex.ONE))
        assertEquals(Complex.ONE, pow(1, Complex.I))
        assertEquals(Complex.ONE, pow(1, w0))
        assertEquals(Complex.ONE, pow(1, w1))
        assertEquals(Complex.ONE, pow(1, w2))
        assertEquals(Complex.NaN, pow(1, Complex.INF))
        assertEquals(Complex.NaN, pow(1, Complex.NaN))

        assertEquals(Complex.ONE, pow(E, Complex.ZERO))
        assertQuasiEquals(E.R, pow(E, Complex.ONE))
        assertQuasiEquals(0.54030230586813 + 0.84147098480790.I, pow(E, Complex.I))
        assertQuasiEquals((-2).R, pow(E, w0))
        assertQuasiEquals(Complex.ONE, pow(E, w1))
        assertQuasiEquals(7.38905609893065.R, pow(E, w2))
        assertEquals(Complex.NaN, pow(E, Complex.INF))
        assertEquals(Complex.NaN, pow(E, Complex.NaN))

        assertQuasiEquals(-1.3171414230751064 - 0.5149159850108396.I, pow(2, w3))
        assertQuasiEquals(-1.7042590259602113 + 0.30903263975372064.I, pow(3, w3))
        assertQuasiEquals(1.4697230567606336 + 1.356434346522595.I, pow(4, w3))
        assertQuasiEquals(-1.6241462732233494 - 1.536928392338012.I, pow(5, w3))
        assertQuasiEquals(-6.366647462900551 + 7.711407140278745.I, pow(100, w3))

        for (w in listOf(0.I, 1.R, 1.I, 3.R, w1, w0, Complex.INF, Complex.NaN)) {
            assertEquals(Complex.NaN, pow(-1, w))
            assertEquals(Complex.NaN, pow(Double.POSITIVE_INFINITY, w))
            assertEquals(Complex.NaN, pow(Double.NEGATIVE_INFINITY, w))
            assertEquals(Complex.NaN, pow(Double.NaN, w))
        }
    }


    @Test
    fun testPowComplexComplex() {
        val w0 = ln(2) + PI.I
        val w1 = 2 * PI.I
        val w2 = 2.R

        assertEquals(Complex.ONE, pow(Complex.ZERO, Complex.ZERO))
        assertEquals(Complex.ZERO, pow(Complex.ZERO, Complex.ONE))
        assertEquals(Complex.ZERO, pow(Complex.ZERO, Complex.I))
        assertEquals(Complex.ZERO, pow(Complex.ZERO, w0))
        assertEquals(Complex.ZERO, pow(Complex.ZERO, w1))
        assertEquals(Complex.ZERO, pow(Complex.ZERO, w2))
        assertEquals(Complex.NaN, pow(Complex.ZERO, Complex.INF))
        assertEquals(Complex.NaN, pow(Complex.ZERO, Complex.NaN))

        assertEquals(Complex.ONE, pow(Complex.ONE, Complex.ZERO))
        assertEquals(Complex.ONE, pow(Complex.ONE, Complex.ONE))
        assertEquals(Complex.ONE, pow(Complex.ONE, 1.I))
        assertEquals(Complex.ONE, pow(Complex.ONE, w0))
        assertEquals(Complex.ONE, pow(Complex.ONE, w1))
        assertEquals(Complex.ONE, pow(Complex.ONE, w2))
        assertEquals(Complex.NaN, pow(Complex.ONE, Complex.INF))
        assertEquals(Complex.NaN, pow(Complex.ONE, Complex.NaN))

        assertEquals(Complex.ONE, pow(E.R, Complex.ZERO))
        assertQuasiEquals(E.R, pow(E.R, Complex.ONE))
        assertQuasiEquals(0.54030230586813 + 0.84147098480790.I, pow(E.R, 1.I))
        assertQuasiEquals((-2).R, pow(E.R, w0))
        assertQuasiEquals(Complex.ONE, pow(E.R, w1))
        assertQuasiEquals(7.38905609893065.R, pow(E.R, w2))
        assertEquals(Complex.NaN, pow(E.R, Complex.INF))
        assertEquals(Complex.NaN, pow(E.R, Complex.NaN))

        assertQuasiEquals(0.20787957635076193.R, pow(1.I, 1.I))
        assertQuasiEquals(0.12900959407446697 + 0.03392409290517014.I, pow((1 + 2.I), (3 + 4.I)))
        assertQuasiEquals(-0.003293803714486435 - 0.031809901650039635.I, pow(w0, w0))
        assertQuasiEquals(9.918799222181348E-5 + 1.764518526767741E-4.I, pow(w0, w1))
        assertQuasiEquals(-9.389151387171156 + 4.355172180607202.I, pow(w0, w2))

        for (w in listOf(Complex.ZERO, Complex.ONE, 1.I, 3.R, w1, w0, Complex.INF, Complex.NaN)) {
            assertEquals(Complex.NaN, pow(Complex.INF, w))
            assertEquals(Complex.NaN, pow(Complex.NaN, w))
        }
    }

    @Test
    fun testPowZetaValues() {
        val w = 0.5 + 14.134725141734695.I
        assertQuasiEquals(-1.3171414230751064 - 0.5149159850108396.I, pow(2, w))
        assertQuasiEquals(-1.7042590259602113 + 0.30903263975372064.I, pow(3, w))
        assertQuasiEquals(1.4697230567606336 + 1.356434346522595.I, pow(4, w))
        assertQuasiEquals(-1.6241462732233494 - 1.536928392338012.I, pow(5, w))
        assertQuasiEquals(-6.366647462900551 + 7.711407140278745.I, pow(100, w))
    }

}
