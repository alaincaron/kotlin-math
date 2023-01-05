package org.alc.utils

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TryTest {
    @Test
    fun success() {
        val a = Try { 2 }
        assertTrue(a.isSuccess)
        assertEquals(2, a.get())
        assertFalse(a.isFailure)
    }

    @Test
    fun failure() {
        val a = Try { throw RuntimeException() }
        assertFalse(a.isSuccess)
        assertTrue(a.isFailure)
        assertThrows<RuntimeException> { a.get() }

    }
}
