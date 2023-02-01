package org.alc.math.vector

import org.alc.math.fix0
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DoubleVectorTest {

    @Test
    fun add() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = doubleArrayOf(2.0, 3.0)
        val z = x + y
        assertContentEquals(doubleArrayOf(3.0, 5.0), z)
    }

    @Test
    fun minus() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = doubleArrayOf(2.0, 3.0)
        val z = x - y
        assertContentEquals(doubleArrayOf(-1.0, -1.0), z)
    }

    @Test
    fun times() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = doubleArrayOf(2.0, 3.0)
        assertEquals(8.0, x * y)
        assertEquals(8.0, x dot y)
    }

    @Test
    fun norm() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = x.norm()
        assertEquals(sqrt(5.0), y)
    }

    @Test
    fun unaryMinus() {
        val x = doubleArrayOf(1.0, 2.0, -3.0)
        assertContentEquals(doubleArrayOf(-1.0, -2.0, 3.0), -x)
    }

    @Test
    fun unaryPlus() {
        val x = doubleArrayOf(1.0, 2.0, -3.0)
        assertSame(x, +x)
    }

    @Test
    fun crossProduct() {
        val x0 = doubleArrayOf(1.0, 2.0)
        assertThrows<IllegalArgumentException> { x0 cross x0 }
        val x1 = doubleArrayOf(1.0, 2.0, 3.0)
        val x2 = doubleArrayOf(2.0, 5.0, 7.0)

        assertContentEquals(DoubleArray(3), x1 cross x1)
        assertContentEquals(doubleArrayOf(-1.0, -1.0, 1.0), x1 cross x2)
        assertContentEquals(doubleArrayOf(1.0, 1.0, -1.0), x2 cross x1)

        val x = doubleArrayOf(1.0, 0.0, 0.0)
        val y = doubleArrayOf(0.0, 1.0, 0.0)
        val z = doubleArrayOf(0.0, 0.0, 1.0)

        assertContentEquals(z, x cross y)
        assertContentEquals(z.unaryMinus().transform(::fix0), y cross x)

        assertContentEquals(y.unaryMinus().transform(::fix0), x cross z)
        assertContentEquals(y, z cross x)

        assertContentEquals(x, y cross z)
        assertContentEquals(x.unaryMinus().transform(::fix0), z cross y)
    }

}
