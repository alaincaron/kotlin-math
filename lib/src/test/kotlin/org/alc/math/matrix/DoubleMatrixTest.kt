package org.alc.math.matrix

import org.alc.math.rational.over
import org.alc.util.matrix.Matrix
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals


class DoubleMatrixTest {

    @Test
    fun gaussianElimination() {
        val m = DoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 3.0
        m[1, 0] = 4.0
        m[1, 1] = -1.0
        val v = DoubleArray(2)
        v[0] = 5.0
        v[1] = 3.0
       assertEquals(listOf(1.0, 1.0), m.solve(v).toList())
    }

    @Test
    fun determinant() {
        val m = DoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 3.0
        m[1, 0] = 4.0
        m[1, 1] = -1.0
        val m1 = DoubleMatrix(m)
        val d = m.determinant()
        assertEquals(-14.0, d)
        assertEquals(m, m1)
    }

    @Test
    fun determinant2() {
        val m = DoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 3.0
        m[1, 0] = 4.0
        m[1, 1] = -1.0
        val m1 = DoubleMatrix(m)
        val d = m.determinant()
        assertEquals(-14.0, d)
        assertEquals(m, m1)
    }


    @Test
    fun identity() {
        val m = DoubleMatrix.identity(10)
        val m1 = DoubleMatrix(m)
        assertEquals(1.0, m.determinant())
        assertEquals(m, m.invert())
        assertEquals(m, m1)
    }

    @Test
    fun singularMatrix() {
        val m = DoubleMatrix(2, 2)
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
        val m = DoubleMatrix(2, 4)
        m[0, 0] = 3.0
        m[0, 1] = 4.0
        m[1, 0] = 2.0
        m[1, 1] = 3.0

        m[0, 2] = 1.0
        m[0, 3] = 0.0
        m[1, 2] = 0.0
        m[1, 3] = 1.0

        DoubleMatrix.GaussianSolver(m).invert()
        assertEquals(1.0, m[0,0], 1e-15)
        assertEquals(0.0, m[0,1], 1e-15)
        assertEquals(0.0, m[1,0], 1e-15)
        assertEquals(1.0, m[1,1], 1e-15)

        assertEquals(3.0, m[0,2], 1e-14)
        assertEquals(-4.0, m[0,3], 1e-14)
        assertEquals(-2.0, m[1,2], 1e-14)
        assertEquals(3.0, m[1,3], 1e-14)
    }

    @Test
    fun invert2() {
        val m = DoubleMatrix(2, 2)
        m[0, 0] = 3.0
        m[0, 1] = 4.0
        m[1, 0] = 2.0
        m[1, 1] = 3.0

        val m2 = m.invert()
        assertEquals(3.0, m2[0,0], 1e-14)
        assertEquals(-4.0, m2[0,1], 1e-14)
        assertEquals(-2.0, m2[1,0], 1e-14)
        assertEquals(3.0, m2[1,1], 1e-14)
    }

    @Test
    fun polynomialInterpolation() {
        // 3 points: (1,-1), (2,1), (3, 5)
        // polynomial interpolation
        // => x^2 - x - 1
        val v = doubleArrayOf(-1.0, 1.0, 5.0)
        val m = DoubleMatrix(3, 3)
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

    @Test
    fun addition() {
        val m1 = Matrix(2,2) { i, j -> (i + j).toDouble()}
        val m2 =  Matrix(2,2) { i, j -> 2.0 * (i + j)}
        val m3 = m1 + m2
        val m4 = Matrix(2,2) { i,j -> 3.0 * (i + j)}
        assertEquals(m3, m4)
    }

    @Test
    fun subtraction() {
        val m1 = Matrix(2,2) { i, j -> (i + j).toDouble()}
        val m2 =  Matrix(2,2) { i, j -> 2.0 * (i + j)}
        val m3 = m2 - m1
        assertEquals(m3, m1)
    }

    @Test fun test123() {
        val m = DoubleMatrix(3, 3)
        m[0, 0] = 1.0
        m[0, 1] = 2.0
        m[0, 2] = 3.0

        m[1,0] = 3.0
        m[1,1] = 4.0
        m[1,2] = 6.0

        m[2,0] = 6.0
        m[2,1] = 7.0
        m[2,2] = 8.0

        assertEquals(5.0, m.determinant(), 1e-13)
        val m2 = Matrix(m).swapColumns(2,0)

        assertEquals(5.0, m.determinant(), 1e-13)
        assertEquals(-5.0, m2.determinant(), 1e-13)
        println("m1.determinant = ${m2.determinant()}")
    }
}
