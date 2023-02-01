package org.alc.math.vector

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.alc.math.rational.*
import kotlin.test.assertContentEquals

class RationalVectorTest {

    @Test
    fun add() {
        val x = RationalVector(1,2)
        val y = RationalVector(2, 3)
        val z = x + y
        assertContentEquals(RationalVector(3, 5), z)
    }

    @Test
    fun minus() {
        val x = RationalVector(1, 2)
        val y = RationalVector(2, 3)
        val z = x - y
        assertContentEquals(RationalVector(-1, -1), z)
    }

    @Test
    fun times() {
        val x = RationalVector(1, 2)
        val y = RationalVector(2, 3)
        assertEquals(8 over 1, x * y)
        assertEquals(8 over 1, x dot y)
    }

    @Test
    fun norm() {
        val x = RationalVector(1, 2)
        val y = x.norm()
        assertEquals(sqrt(5.0), y)
    }

    @Test
    fun normSquare() {
     assertEquals(5 over 1, RationalVector(1,2).normSquare())
    }

    @Test
    fun unaryMinus() {
        val x = RationalVector(1.0, 2.0, -3.0)
        assertContentEquals(RationalVector(-1.0, -2.0, 3.0), -x)
    }

    @Test
    fun unaryPlus() {
        val x = RationalVector(1.0, 2.0, -3.0)
        assertSame(x, +x)
    }

    @Test
    fun crossProduct() {
        val x0 = RationalVector(1.0, 2.0)
        assertThrows<IllegalArgumentException> { x0 cross x0 }
        val x1 = RationalVector(1.0, 2.0, 3.0)
        val x2 = RationalVector(2.0, 5.0, 7.0)

        assertContentEquals(Array(3) { Rational.ZERO }, x1 cross x1)
        assertContentEquals(RationalVector(-1.0, -1.0, 1.0), x1 cross x2)
        assertContentEquals(RationalVector(1.0, 1.0, -1.0), x2 cross x1)

        val x = RationalVector(1.0, 0.0, 0.0)
        val y = RationalVector(0.0, 1.0, 0.0)
        val z = RationalVector(0.0, 0.0, 1.0)

        assertContentEquals(z, x cross y)
        assertContentEquals(-z, y cross x)

        assertContentEquals(-y, x cross z)
        assertContentEquals(y, z cross x)

        assertContentEquals(x, y cross z)
        assertContentEquals(-x, z cross y)
    }

}
