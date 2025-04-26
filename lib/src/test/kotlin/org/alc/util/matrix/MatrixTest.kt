package org.alc.util.matrix

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class MatrixTest {

    @Test
    fun createMatrix() {
        val a = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(3, a[1, 1])
        assertThrows<ArrayIndexOutOfBoundsException> { a[0, 5] }
        assertThrows<ArrayIndexOutOfBoundsException> { a[5, 0] }
    }

    @Test
    fun transpose() {
        val a = Matrix(2, 3) { i, j -> 3 * i + j }
        val b = Matrix(a)
        val c = a.transpose()
        assertEquals(b, a)
        val d = c.transpose()
        assertEquals(d, a)
    }

    @Test
    fun map() {
        val a = Matrix(2, 3) { i, j -> 3 * i + j }
        val b = Matrix(a)
        val c = a.map { x -> x + 1 }
        val d = Matrix(2, 3) { i, j -> 3 * i + j + 1 }
        assertEquals(b, a)
        assertEquals(d, c)
    }

    @Test
    fun mapIndexed() {
        val a = Matrix(2, 3) { i, j -> 3 * i + j }
        val b = Matrix(a)
        val c = a.mapIndexed { i, j, x -> 2 * i + 2 * j + x }
        val d = Matrix(2, 3) { i, j -> 5 * i + 3 * j }
        assertEquals(b, a)
        assertEquals(d, c)
    }

    @Test
    fun transform() {
        val a = Matrix(2, 3) { i, j -> 3 * i + j }
        a.transform { x -> x + 1 }
        val d = Matrix(2, 3) { i, j -> 3 * i + j + 1 }
        assertEquals(d, a)

    }

    @Test
    fun transformIndexed() {
        val a = Matrix(2, 3) { i, j -> 3 * i + j }
        a.transformIndexed { i, j, x -> 2 * i + 2 * j + x }
        val d = Matrix(2, 3) { i, j -> 5 * i + 3 * j }
        assertEquals(d, a)
    }

    @Test
    fun isSquare() {
        assertFalse(Matrix(2, 3, 0).isSquare())
        assertTrue(Matrix(2, 2, 0).isSquare())
    }

    @Test
    fun forEach() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        var sum = 0
        m.forEach { sum += it }
        assertEquals(6, sum)
    }

    @Test
    fun forEachIndexed() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        var sum = 0
        m.forEachIndexed { i, j, v -> sum += i + j + v }
        assertEquals(10, sum)
    }

    @Test
    fun rowForEach() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        var sum = 0
        m.rowForEach(0) { sum += it }
        assertEquals(1, sum)
        sum = 0
        m.rowForEach(1) { sum += it }
        assertEquals(5, sum)
    }

    @Test
    fun columnForEach() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        var sum = 0
        m.columnForEach(0) { sum += it }
        assertEquals(2, sum)
        sum = 0
        m.columnForEach(1) { sum += it }
        assertEquals(4, sum)
    }

    @Test
    fun rowForEachIndexed() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        var sum = 0
        m.rowForEachIndexed(0) { i, j, v -> sum += i + j + v }
        assertEquals(2, sum)
        sum = 0
        m.rowForEachIndexed(1) { i, j, v -> sum += i + j + v }
        assertEquals(8, sum)
    }

    @Test
    fun columnForEachIndexed() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        var sum = 0
        m.columnForEachIndexed(0) { i, j, v -> sum += i + j + v }
        assertEquals(3, sum)
        sum = 0
        m.columnForEachIndexed(1) { i, j, v -> sum += i + j + v }
        assertEquals(7, sum)
    }

    @Test
    fun reduce() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(11, m.reduce(5) { acc, v -> acc + v })
    }

    @Test
    fun reduceIndexed() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(15, m.reduceIndexed(5) { acc, v, i, j -> acc + v + i + j })
    }

    @Test
    fun rowReduce() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(6, m.rowReduce(0, 5) { acc, v -> acc + v })
        assertEquals(15, m.rowReduce(1, 10) { acc, v -> acc + v })
    }

    @Test
    fun columnReduce() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(7, m.columnReduce(0, 5) { acc, v -> acc + v })
        assertEquals(14, m.columnReduce(1, 10) { acc, v -> acc + v })
    }

    @Test
    fun rowReduceIndexed() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(7, m.rowReduceIndexed(0,5) { acc, v, i, j -> acc + v + i + j })
        assertEquals(13, m.rowReduceIndexed(1,5) { acc, v, i, j -> acc + v + i + j })
    }

    @Test
    fun columnReduceIndexed() {
        val m = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(8, m.columnReduceIndexed(0,5) { acc, v, i, j -> acc + v + i + j })
        assertEquals(12, m.columnReduceIndexed(1,5) { acc, v, i, j -> acc + v + i + j })
    }
}
