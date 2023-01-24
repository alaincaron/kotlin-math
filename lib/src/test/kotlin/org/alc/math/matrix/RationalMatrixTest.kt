package org.alc.math.matrix

import org.alc.math.rational.Rational
import org.alc.math.rational.over
import org.alc.math.rational.plus
import org.alc.math.rational.toRational
import org.alc.util.matrix.Matrix
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals


class RationalMatrixTest {

    @Test
    fun gaussianElimination() {
        val m = RationalMatrix(2, 2)
        m[0, 0] = 2 over 1
        m[0, 1] = 3 over 1
        m[1, 0] = 4 over 1
        m[1, 1] = -1 over 1
        val v = arrayOf(5 over 1, 3 over 1)
       assertEquals(listOf(Rational.ONE, Rational.ONE), m.solve(v).toList())
    }

    @Test
    fun determinant() {
        val m = RationalMatrix(2, 2)
        m[0, 0] = 2 over 1
        m[0, 1] = 3 over 1
        m[1, 0] = 4 over 1
        m[1, 1] = -1 over 1
        val m1 = RationalMatrix(m)
        val d = m.determinant()
        assertEquals(-14 over 1, d)
        assertEquals(m, m1)
    }

    @Test
    fun determinant2() {
        val m = RationalMatrix(2, 2)
        m[0, 0] = 2 over 1
        m[0, 1] = 3 over 1
        m[1, 0] = 4 over 1
        m[1, 1] = -1 over 1
        val m1 = RationalMatrix(m)
        val d = m.determinant()
        assertEquals(-14 over 1, d)
        assertEquals(m, m1)
    }


    @Test
    fun identity() {
        val m = RationalMatrix.identity(10)
        val m1 = RationalMatrix(m)
        assertEquals(Rational.ONE, m.determinant())
        assertEquals(m, m.invert())
        assertEquals(m, m1)
    }

    @Test
    fun singularMatrix() {
        val m = RationalMatrix(2, 2)
        m[0, 0] = 1 over 1
        m[0, 1] = 2 over 1
        m[1, 0] = 2 over 1
        m[1, 1] = 4 over 1
        assertEquals(Rational.ZERO, m.determinant())
        assertThrows<ArithmeticException> { m.invert() }
        assertThrows<ArithmeticException> { m.solve(Array(2) { i -> i + Rational.ONE }) }
    }

    @Test
    fun invert1() {
        val m = RationalMatrix(2, 4)
        m[0, 0] = 3 over 1
        m[0, 1] = 4 over 1
        m[1, 0] = 2 over 1
        m[1, 1] = 3 over 1

        m[0, 2] = 1 over 1
        m[0, 3] = 0 over 1
        m[1, 2] = 0 over 1
        m[1, 3] = 1 over 1

        RationalMatrix.GaussianSolver(m).invert()
        assertSame(Rational.ONE, m[0,0])
        assertSame(Rational.ZERO, m[0,1])
        assertSame(Rational.ZERO, m[1,0])
        assertSame(Rational.ONE, m[1,1])

        assertEquals(3 over 1, m[0,2])
        assertEquals(-4 over 1, m[0,3])
        assertEquals(-2 over 1, m[1,2])
        assertEquals(3 over 1, m[1,3])
    }

    @Test
    fun invert2() {
        val m = RationalMatrix(2, 2)
        m[0, 0] = 3 over 1
        m[0, 1] = 4 over 1
        m[1, 0] = 2 over 1
        m[1, 1] = 3 over 1

        val m2 = m.invert()
        assertEquals(3 over 1, m2[0,0])
        assertEquals(-4 over 1, m2[0,1])
        assertEquals(-2 over 1, m2[1,0])
        assertEquals(3 over 1, m2[1,1])
    }

    @Test
    fun polynomialInterpolation() {
        // 3 points: (1,-1), (2,1), (3, 5)
        // polynomial interpolation
        // => x^2 - x - 1
        val v = arrayOf(-1 over 1, 1 over 1, 5 over 1)
        val m = RationalMatrix(3, 3)
        m[0, 0] = 1 over 1
        m[0, 1] = 1 over 1
        m[0, 2] = 1 over 1
        m[1, 0] = 4 over 1
        m[1, 1] = 2 over 1
        m[1, 2] = 1 over 1
        m[2, 0] = 9 over 1
        m[2, 1] = 3 over 1
        m[2, 2] = 1 over 1

        val soln = m.solve(v)
        assertEquals(listOf(1 over 1, -1 over 1, -1 over 1), soln.toList())
    }

    @Test
    fun addition() {
        val m1 = Matrix(2,2) { i, j -> (i + j).toRational()}
        val m2 =  Matrix(2,2) { i, j -> Rational.TWO * (i + j)}
        val m3 = m1 + m2
        val m4 = Matrix(2,2) { i,j -> (3 over 1) * (i + j)}
        assertEquals(m3, m4)
    }

    @Test
    fun subtraction() {
        val m1 = Matrix(2,2) { i, j -> (i + j).toRational()}
        val m2 =  Matrix(2,2) { i, j -> Rational.TWO * (i + j)}
        val m3 = m2 - m1
        assertEquals(m3, m1)
    }

    @Test fun test123() {
        val m = RationalMatrix(3, 3)
        m[0, 0] = 1 over 1
        m[0, 1] = 2 over 1
        m[0, 2] = 3 over 1

        m[1,0] = 3 over 1
        m[1,1] = 4 over 1
        m[1,2] = 6 over 1

        m[2,0] = 6 over 1
        m[2,1] = 7 over 1
        m[2,2] = 8 over 1
        assertEquals(5 over 1, m.determinant())

        val m1 = Matrix(m).swapColumns(2,0)
        assertEquals(-5 over 1, m1.determinant())

    }

}
