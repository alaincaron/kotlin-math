package org.alc.monad

import org.junit.jupiter.api.assertThrows
import java.lang.RuntimeException
import kotlin.test.*

class OptionTest {

    @Test
    fun empty() {
        assertSame(None, Option.empty())
        assertSame(None, Option.ofNullable(null))
        assertTrue(None.isEmpty())
        assertFalse(None.isDefined())
        assertFalse(None.isNotEmpty())
        assertThrows<NoSuchElementException> { None.get() }
        assertNull(None.getOrNull())
        assertEquals(5, None.getOrElse { 5 })
        assertEquals(Some(5), None.orElse { Some(5) })
    }

    @Test
    fun testNotEmpty() {
        val a = Option(2)
        assertEquals(Some(2), a)
        assertFalse(a.isEmpty())
        assertTrue(a.isDefined())
        assertTrue(a.isNotEmpty())
        assertEquals(2, a.get())
        assertEquals(2, a.getOrNull())
        assertEquals(2, a.getOrElse { throw RuntimeException() })
        assertSame(a, a.orElse { throw RuntimeException() })
    }

    @Test
    fun unless() {
        assertEquals(Some(2), Option.unless(false) { 2 })
        assertSame(None, Option.unless(true) { 2 })
    }

    @Test
    fun createIf() {
        assertEquals(Some(2), Option.createIf(true) { 2 })
        assertSame(None, Option.createIf(false) { 2 })
    }

    @Test
    fun onNone() {
        var counter = 0
        val f = { ++counter }
        val a = Some(2)
        assertSame(a, a.onNone(f))
        assertEquals(0, counter)
        assertSame(None, None.onNone(f))
        assertEquals(1, counter)
    }

    @Test
    fun onSome() {
        var counter = 1
        val f = { i: Int -> counter += i }
        val a = Some(2)
        assertSame(a, a.onSome(f))
        assertEquals(3, counter)
        assertSame(None, None.onSome(f))
        assertEquals(3, counter)
    }

    @Test
    fun fold() {
        val ifNone = { 2 }
        val ifSome = { i: Int -> i + 1}

        assertEquals(2, None.fold(ifNone, ifSome))
        assertEquals(3, Some(2).fold(ifNone, ifSome))
    }

    @Test
    fun map() {
        val f = { i: Int ->  i + 1 }
        assertEquals(Some(3), Some(2).map(f))
    }

    @Test
    fun flatMap() {
        val f = { i: Int -> if (i < 0) None else Some(i + 1) }
        assertEquals(None, Some(-1).flatMap(f))
        assertEquals(None, None.flatMap(f))
        assertEquals(Some(3), Some(2).flatMap(f))
    }

    @Test
    fun all() {
        assertTrue(None.all { false })
        assertTrue(Some(2).all { it == 2})
        assertFalse(Some(2).all {it != 2 })
    }

    @Test
    fun filter() {
        assertEquals(None, None.filter { true })
        assertEquals(Some(2), Some(2).filter { it == 2})
        assertEquals(None, Some(2).filter {it != 2 })
    }

    @Test
    fun filterNot() {
        assertEquals(None, None.filterNot { true })
        assertEquals(None, Some(2).filterNot { it == 2})
        assertEquals(Some(2), Some(2).filterNot {it != 2 })
    }

    @Test
    fun exists() {
        assertFalse(None.exists { true })
        assertTrue(Some(2).exists { it == 2})
        assertFalse(Some(2).exists {it != 2 })
    }

    @Test
    fun maybe() {
        val a = 2
        val b = null
        assertEquals(Some(a), a.maybe())
        assertEquals(None, a.toNone())
        assertEquals(None, b.maybe())
        assertEquals(None, b.toNone())
    }

    @Test
    fun maybeBool() {
        assertEquals(Some(2), true.maybe { 2 })
        assertEquals(None, false.maybe { 2 })
    }

    @Test
    fun flatten() {
        assertEquals(Some(2), Some(Some(2)).flatten())
        assertEquals(None, None.flatten())
        assertEquals(None, Some(None).flatten())
    }

    @Test
    fun widen() {
        val a = Some(5)
        val b: Option<Number> = a.widen()
        assertEquals(5, b.get())
        assertSame(a, b)

        val c: Option<Int> = None
        val d: Option<Number> = c.widen()
        assertSame(c, d)
    }
}
