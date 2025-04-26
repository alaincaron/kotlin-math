package org.alc.math.vector

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LongVectorTest {

    @Test
    fun add() {
        val x = longArrayOf(1, 2)
        val y = longArrayOf(2, 3)
        val z = x + y
        assertContentEquals(longArrayOf(3, 5), z)
    }

    @Test
    fun minus() {
        val x = longArrayOf(1, 2)
        val y = longArrayOf(2, 3)
        val z = x - y
        assertContentEquals(longArrayOf(-1, -1), z)
    }

    @Test
    fun times() {
        val x = longArrayOf(1, 2)
        val y = longArrayOf(2, 3)
        assertEquals(8, x * y)
        assertEquals(8, x dot y)
    }

    @Test
    fun norm() {
        val x = longArrayOf(1, 2)
        val y = x.norm()
        assertEquals(sqrt(5.0), y)
    }

    @Test
    fun unaryMinus() {
        val x = longArrayOf(1, 2, -3)
        assertContentEquals(longArrayOf(-1, -2, 3), -x)
    }

    @Test
    fun unaryPlus() {
        val x = longArrayOf(1, 2, -3)
        assertSame(x, +x)
    }

    @Test
    fun crossProduct() {
        val x0 = longArrayOf(1, 2)
        assertThrows<IllegalArgumentException> { x0 cross x0 }
        val x1 = longArrayOf(1, 2, 3)
        val x2 = longArrayOf(2, 5, 7)

        assertContentEquals(LongArray(3), x1 cross x1)
        assertContentEquals(longArrayOf(-1, -1, 1), x1 cross x2)
        assertContentEquals(longArrayOf(1, 1, -1), x2 cross x1)

        val x = longArrayOf(1, 0, 0)
        val y = longArrayOf(0, 1, 0)
        val z = longArrayOf(0, 0, 1)

        assertContentEquals(z, x cross y)
        assertContentEquals(-z, y cross x)

        assertContentEquals(-y, x cross z)
        assertContentEquals(y, z cross x)

        assertContentEquals(x, y cross z)
        assertContentEquals(-x, z cross y)
    }

}
