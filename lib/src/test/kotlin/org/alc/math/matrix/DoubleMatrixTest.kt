package org.alc.math.matrix

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals


class DoubleMatrixTest {

    @Test
    fun gaussianElimination() {
        val m = MutableDoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 3.0
        m[1, 0] = 4.0
        m[1, 1] = -1.0
        val v = DoubleArray(2)
        v[0] = 5.0
        v[1] = 3.0
        val x = m.solve(v).toList()
        println("x = $x")
    }

    @Test
    fun determinant() {
        val m = MutableDoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 3.0
        m[1, 0] = 4.0
        m[1, 1] = -1.0
        val m1 = MutableDoubleMatrix(m)
        val d = m.determinant()
        println("d = $d")
        assertEquals(-14.0, d)
        assertEquals(m, m1)
    }

    @Test
    fun determinant2() {
        val m = MutableDoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 3.0
        m[1, 0] = 4.0
        m[1, 1] = -1.0
        val m1 = MutableDoubleMatrix(m)
        val d = m.determinant()
        println("d = $d")
        assertEquals(-14.0, d)
        assertEquals(m, m1)
    }


    @Test
    fun identity() {
        val m = MutableDoubleMatrix.identity(10)
        val m1 = MutableDoubleMatrix(m)
        assertEquals(1.0, m.determinant())
        assertEquals(m, m.invert())
        assertEquals(m, m1)
    }

    @Test
    fun singularMatrix() {
        val m = MutableDoubleMatrix(2, 2)
        m[0, 0] = 1.0
        m[0, 1] = 2.0
        m[1, 0] = 2.0
        m[1, 1] = 4.0
        assertEquals(0.0, m.determinant())
        assertThrows<ArithmeticException> { m.invert() }
        assertThrows<ArithmeticException> { m.solve(DoubleArray(2) { i -> i + 1.0 }) }
    }

    @Test
    fun invert1() {
        val m = MutableDoubleMatrix(2, 4)
        m[0, 0] = 3.0
        m[0, 1] = 4.0
        m[1, 0] = 2.0
        m[1, 1] = 3.0

        m[0, 2] = 1.0
        m[0, 3] = 0.0
        m[1, 2] = 0.0
        m[1, 3] = 1.0

        GaussianElimination(m).invert()
        println("m =\n$m")
    }

    @Test
    fun invert2() {
        val m = MutableDoubleMatrix(2, 2)
        m[0, 0] = 3.0
        m[0, 1] = 4.0
        m[1, 0] = 2.0
        m[1, 1] = 3.0

        val m2 = m.invert()
        println("m =\n$m2")
    }

    @Test
    fun polynomialInterpolation() {
        // 3 points: (1,-1), (2,1), (3, 5)
        // polynomial interpolation
        // => x^2 - x - 1
        val v = doubleArrayOf(-1.0, 1.0, 5.0)
        val m = MutableDoubleMatrix(3, 3)
        m[0, 0] = 1.0
        m[0, 1] = 1.0
        m[0, 2] = 1.0
        m[1, 0] = 4.0
        m[1, 1] = 2.0
        m[1, 2] = 1.0
        m[2, 0] = 9.0
        m[2, 1] = 3.0
        m[2, 2] = 1.0

        val soln = m.solve(v)
        assertEquals(listOf(1.0, -1.0, -1.0), soln.toList())
    }
}
