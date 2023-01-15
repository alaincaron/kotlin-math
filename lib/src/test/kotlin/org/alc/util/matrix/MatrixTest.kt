package org.alc.util.matrix

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals


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
}
