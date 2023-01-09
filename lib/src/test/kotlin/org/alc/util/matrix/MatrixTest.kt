package org.alc.util.matrix

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals


class MatrixTest {
    @Test
    fun createMutableMatrix() {
        val a = MutableMatrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(0, a[0, 0])
        a[0, 0] = 10
        assertEquals(10, a[0, 0])
        assertThrows<ArrayIndexOutOfBoundsException> {  a[0, 5] = 15 }
        assertThrows<ArrayIndexOutOfBoundsException> {  a[5, 0] = 15 }
    }

    @Test
    fun createMatrix() {
        val a = Matrix(2, 2) { i, j -> 2 * i + j }
        assertEquals(3, a[1, 1])
        assertThrows<ArrayIndexOutOfBoundsException> {  a[0, 5] }
        assertThrows<ArrayIndexOutOfBoundsException> {  a[5, 0] }
    }
}
