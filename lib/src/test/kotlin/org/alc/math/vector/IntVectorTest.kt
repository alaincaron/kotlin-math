package org.alc.math.vector

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertSame

class IntVectorTest {

    @Test
    fun add() {
        val x = IntVector(1, 2)
        val y = IntVector(2, 3)
        val z = x + y
        assertEquals(IntVector(3, 5), z)
    }

    @Test
    fun minus() {
        val x = IntVector(1, 2)
        val y = IntVector(2, 3)
        val z = x - y
        assertEquals(IntVector(-1, -1), z)
    }

    @Test
    fun times() {
        val x = IntVector(1, 2)
        val y = IntVector(2, 3)
        assertEquals(8, x * y)
        assertEquals(8, x dot y)
    }

    @Test
    fun norm() {
        val x = IntVector(1, 2)
        val y = x.norm()
        assertEquals(sqrt(5.0), y)
    }

    @Test
    fun unaryMinus() {
        val x = IntVector(1,2,-3)
        assertEquals(IntVector(-1,-2,3), -x)
    }

    @Test fun unaryPlus() {
        val x = IntVector(1,2,-3)
        assertSame(x, +x)
    }

    @Test
    fun crossProduct() {
        val x0 = IntVector(1, 2)
        assertThrows<IllegalArgumentException> { x0 cross x0 }
        val x1 = IntVector(1, 2, 3)
        val x2 = IntVector(2, 5, 7)

        assertEquals(IntVector(3) { 0 }, x1 cross x1)
        assertEquals(IntVector(-1, -1, 1), x1 cross x2)
        assertEquals(IntVector(1,1,-1), x2 cross x1)

        val x = IntVector(1,0,0)
        val y = IntVector(0,1,0)
        val z = IntVector(0,0,1)

        assertEquals(z, x cross y)
        assertEquals(-z, y cross x)

        assertEquals(-y, x cross z)
        assertEquals(y, z cross x)

        assertEquals(x, y cross z)
        assertEquals(-x, z cross y)
    }

}
