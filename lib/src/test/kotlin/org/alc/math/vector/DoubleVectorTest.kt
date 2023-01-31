package org.alc.math.vector

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DoubleVectorTest {

    @Test
    fun add() {
        val x = DoubleVector(1.0, 2.0)
        val y = DoubleVector(2.0, 3.0)
        val z = x + y
        assertEquals(DoubleVector(3.0, 5.0), z)
    }

    @Test
    fun minus() {
        val x = DoubleVector(1.0, 2.0)
        val y = DoubleVector(2.0, 3.0)
        val z = x - y
        assertEquals(DoubleVector(-1.0, -1.0), z)
    }

    @Test
    fun times() {
        val x = DoubleVector(1.0, 2.0)
        val y = DoubleVector(2.0, 3.0)
        assertEquals(8.0, x * y)
        assertEquals(8.0, x dot y)
    }

    @Test
    fun norm() {
        val x = DoubleVector(1.0, 2.0)
        val y = x.norm()
        assertEquals(sqrt(5.0), y)
    }

    @Test
    fun unaryMinus() {
        val x = DoubleVector(1.0, 2.0, -3.0)
        assertEquals(DoubleVector(-1.0, -2.0, 3.0), -x)
    }

    @Test
    fun unaryPlus() {
        val x = DoubleVector(1.0, 2.0, -3.0)
        assertSame(x, +x)
    }

    @Test
    fun crossProduct() {
        val x0 = DoubleVector(1.0, 2.0)
        assertThrows<IllegalArgumentException> { x0 cross x0 }
        val x1 = DoubleVector(1.0, 2.0, 3.0)
        val x2 = DoubleVector(2.0, 5.0, 7.0)

        assertEquals(DoubleVector(3) { 0.0 }, x1 cross x1)
        assertEquals(DoubleVector(-1.0, -1.0, 1.0), x1 cross x2)
        assertEquals(DoubleVector(1.0, 1.0, -1.0), x2 cross x1)

        val x = DoubleVector(1.0, 0.0, 0.0)
        val y = DoubleVector(0.0, 1.0, 0.0)
        val z = DoubleVector(0.0, 0.0, 1.0)

        assertEquals(z, x cross y)
        assertEquals(-z, y cross x)

        assertEquals(-y, x cross z)
        assertEquals(y, z cross x)

        assertEquals(x, y cross z)
        assertEquals(-x, z cross y)
    }

}
