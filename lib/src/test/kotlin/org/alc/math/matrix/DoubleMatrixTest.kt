package org.alc.math.matrix

import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.jupiter.api.assertThrows


class DoubleMatrixTest {

    @Test
    fun gaussianElimination() {
        val m = MutableDoubleMatrix(2,3)
        m[0,0] = 2.0
        m[0,1] = 3.0
        m[0,2] = 5.0
        m[1,0] = 4.0
        m[1,1] = -1.0
        m[1,2] = 3.0
        println("m = $m")
        val x = m.solve().toList()
        println("x = $x")
    }

    @Test
    fun determinant() {
        val m = MutableDoubleMatrix(2,2)
        m[0,0] = 2.0
        m[0,1] = 3.0
        m[1,0] = 4.0
        m[1,1] = -1.0
        val d = m.determinant()
        println("d = $d")
        assertEquals(-14.0, d)
    }

    @Test fun identity() {
        val m = MutableDoubleMatrix.identity(10)
        assertEquals(1.0, m.determinant())
    }

    @Test
    fun singularMatrix() {
        val m = MutableDoubleMatrix(2,2)
        m[0,0] = 1.0
        m[0,1] = 2.0
        m[1,0] = 2.0
        m[1,1] = 4.0
        assertEquals(0.0, m.determinant())
        assertThrows<ArithmeticException> { m.solve()}

    }
}
