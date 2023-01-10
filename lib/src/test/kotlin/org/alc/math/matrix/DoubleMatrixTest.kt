package org.alc.math.matrix

import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows


class DoubleMatrixTest {

    @Test
    fun gaussianElimination() {
        val m = MutableDoubleMatrix(2,2)
        m[0,0] = 2.0
        m[0,1] = 3.0
        m[1,0] = 4.0
        m[1,1] = -1.0
        val v = DoubleArray(2)
        v[0] = 5.0
        v[1] = 3.0
        val x = GaussianResolver.resolve(m,v).toList()
        println("x = $x")
    }

    @Test
    fun determinant() {
        val m = MutableDoubleMatrix(2,2)
        m[0,0] = 2.0
        m[0,1] = 3.0
        m[1,0] = 4.0
        m[1,1] = -1.0
        val d = GaussianElimination(m).determinant()
        println("d = $d")
        assertEquals(-14.0, d)
    }

    @Test
    fun determinant2() {
        val m = MutableDoubleMatrix(2,2)
        m[0,0] = 2.0
        m[0,1] = 3.0
        m[1,0] = 4.0
        m[1,1] = -1.0
        val d = GaussianResolver.determinant(m)
        println("d = $d")
        assertEquals(-14.0, d)
    }


    @Test fun identity() {
        val m = MutableDoubleMatrix.identity(10)
        assertEquals(1.0, GaussianElimination(m).determinant())
    }

    @Test
    fun singularMatrix() {
        val m = MutableDoubleMatrix(2,2)
        m[0,0] = 1.0
        m[0,1] = 2.0
        m[1,0] = 2.0
        m[1,1] = 4.0
        assertEquals(0.0, GaussianElimination(m).determinant())
        assertThrows<ArithmeticException> { GaussianElimination(m).solve()}
    }

    @Test
    fun invert1() {
        val m = MutableDoubleMatrix(2,4)
        m[0,0] = 3.0
        m[0,1] = 4.0
        m[1,0] = 2.0
        m[1,1] = 3.0

        m[0,2] = 1.0
        m[0,3] = 0.0
        m[1,2] = 0.0
        m[1,3] = 1.0

        GaussianElimination(m).invert()
        println("m =\n$m")
    }

    @Test
    fun invert2() {
        val m = MutableDoubleMatrix(2,2)
        m[0,0] = 3.0
        m[0,1] = 4.0
        m[1,0] = 2.0
        m[1,1] = 3.0

        val m2 = GaussianResolver.invert(m)
        println("m =\n$m2")
    }

    @Test fun polynomialInterpolation() {
        // 3 points: (1,-1), (2,1), (3, 5)
        // polynomial interpolation
        // => x^2 - x - 1
        val v = DoubleArray(3)
        v[0] = -1.0
        v[1] = 1.0
        v[2] =  5.0
        val m = MutableDoubleMatrix(3, 3)
        m[0,0] = 1.0
        m[0,1] = 1.0
        m[0,2]  = 1.0
        m[1,0] = 4.0
        m[1,1] = 2.0
        m[1,2] = 1.0
        m[2,0] =  9.0
        m[2,1] = 3.0
        m[2,2] = 1.0

        val soln = GaussianResolver.resolve(m,v)
        println("soln =${soln.toList()}")

    }

}
