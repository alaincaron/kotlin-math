package org.alc.math.vector

import org.alc.math.fix0
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.math.sqrt
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

class DoubleVectorTest {

    @Test
    fun addPrimitive() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = doubleArrayOf(2.0, 3.0)
        val z = x + y
        assertContentEquals(doubleArrayOf(3.0, 5.0), z)
    }

    @Test
    fun addGeneric() {
        val x = arrayOf(1.0, 2.0)
        val y = arrayOf(2.0, 3.0)
        val z = x + y
        assertContentEquals(arrayOf(3.0, 5.0), z)
    }

    @Test
    fun minusPrimitive() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = doubleArrayOf(2.0, 3.0)
        val z = x - y
        assertContentEquals(doubleArrayOf(-1.0, -1.0), z)
    }

    @Test
    fun minusGeneric() {
        val x = arrayOf(1.0, 2.0)
        val y = arrayOf(2.0, 3.0)
        val z = x - y
        assertContentEquals(arrayOf(-1.0, -1.0), z)
    }

    @Test
    fun timesPrimitive() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = doubleArrayOf(2.0, 3.0)
        assertEquals(8.0, x * y)
        assertEquals(8.0, x dot y)
    }

    @Test
    fun timesGeneric() {
        val x = arrayOf(1.0, 2.0)
        val y = arrayOf(2.0, 3.0)
        assertEquals(8.0, x * y)
        assertEquals(8.0, x dot y)
    }

    @Test
    fun normPrimitive() {
        val x = doubleArrayOf(1.0, 2.0)
        val y = x.norm()
        assertEquals(sqrt(5.0), y)
    }

    @Test
    fun normGeneric() {
        val x = arrayOf(1.0, 2.0)
        val y = x.norm()
        assertEquals(sqrt(5.0), y)
    }

    @Test
    fun unaryMinusPrimitive() {
        val x = doubleArrayOf(1.0, 2.0, -3.0)
        assertContentEquals(doubleArrayOf(-1.0, -2.0, 3.0), -x)
    }

    @Test
    fun unaryMinusGeneric() {
        val x = arrayOf(1.0, 2.0, -3.0)
        assertContentEquals(arrayOf(-1.0, -2.0, 3.0), -x)
    }

    @Test
    fun unaryPlusPrimitive() {
        val x = doubleArrayOf(1.0, 2.0, -3.0)
        val y = +x
        assertContentEquals(x, y)
        assertNotSame(x, y)
    }

    @Test
    fun unaryPlusGeneric() {
        val x = arrayOf(1.0, 2.0, -3.0)
        val y = +x
        assertContentEquals(x, y)
        assertNotSame(x, y)
    }

    @Test
    fun crossProductPrimitive() {
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

    @Test
    fun crossProductGeneric() {
        val x0 = arrayOf(1.0, 2.0)
        assertThrows<IllegalArgumentException> { x0 cross x0 }
        val x1 = arrayOf(1.0, 2.0, 3.0)
        val x2 = arrayOf(2.0, 5.0, 7.0)
        val x3 = x1 cross x1

        assertContentEquals(Array(3) { 0.0 }, x3)
        assertContentEquals(arrayOf(-1.0, -1.0, 1.0), x1 cross x2)
        assertContentEquals(arrayOf(1.0, 1.0, -1.0), x2 cross x1)

        val x = arrayOf(1.0, 0.0, 0.0)
        val y = arrayOf(0.0, 1.0, 0.0)
        val z = arrayOf(0.0, 0.0, 1.0)

        assertContentEquals(z, x cross y)
        assertContentEquals(z.unaryMinus().transform(::fix0), y cross x)

        assertContentEquals(y.unaryMinus().transform(::fix0), x cross z)
        assertContentEquals(y, z cross x)

        assertContentEquals(x, y cross z)
        assertContentEquals(x.unaryMinus().transform(::fix0), z cross y)
    }

    @Test
    fun projectionPrimitive() {
        val v1 = doubleArrayOf(1.0, 2.0)
        val v2 = doubleArrayOf(2.0, -1.0)
        assertContentEquals(DoubleArray(2), (v1 project v2).transform(::fix0))

        val v3 = doubleArrayOf(2.0, 3.0)
        assertContentEquals(v3 * (8.0 / 13.0), v1 project v3)
    }

    @Test
    fun projectionGeneric() {
        val v1 = arrayOf(1.0, 2.0)
        val v2 = arrayOf(2.0, -1.0)
        assertContentEquals(Array(2) { 0.0 }, (v1 project v2).transform(::fix0))

        val v3 = arrayOf(2.0, 3.0)
        assertContentEquals(v3 * (8.0 / 13.0), v1 project v3)
    }

    @Test
    fun transformedIndexedPrimitive() {
        val v = DoubleArray(5) { it.toDouble() }
        v.transformIndexed { i: Int, item: Double -> i.toDouble() + item }
        val v1 = DoubleArray(5) { it.toDouble() * 2.0 }
        assertContentEquals(v1, v)
    }

    @Test
    fun transformedIndexedGeneric() {
        val v = Array(5) { it.toDouble() }
        v.transformIndexed { i: Int, item: Double -> i.toDouble() + item }
        val v1 = Array(5) { it.toDouble() * 2.0 }
        assertContentEquals(v1, v)
    }
}
