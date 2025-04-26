package org.alc.monad

import org.junit.jupiter.api.assertThrows
import kotlin.test.*

class TryTest {
    @Test
    fun getOnSuccess() {
        val a = Try { 2 }
        assertTrue(a.isSuccess)
        assertFalse(a.isFailure)
        assertEquals(Success(2), a)
        assertEquals(2, a.get())
        assertEquals(2, a.getOrElse { throw RuntimeException() })
        assertEquals(Success(2), a.orElse { Try { throw RuntimeException() } })
    }

    @Test
    fun getOnFailure() {
        val e = RuntimeException()
        val a: Try<Int> = Try { throw e }
        assertFalse(a.isSuccess)
        assertTrue(a.isFailure)
        assertEquals(e, assertThrows { a.get() })
        assertEquals(2, a.getOrElse { 2 })
        assertEquals(Success(2), a.orElse { Try { 2 } })
    }

    @Test
    fun map() {
        val a = Try { 2 }
        val e = RuntimeException()
        val f = { x: Int -> 2 * x + 1 }
        assertEquals(Success(5), a.map(f))
        assertIs<Failure<RuntimeException>>(a.map { throw e })

        val b: Try<Int> = Try { throw e }
        val c: Try<Int> = b.map { throw RuntimeException() }
        assertSame(b, c)
        val d = b.map(f)
        assertSame(d, b)
    }

    @Test
    fun flatMap() {
        val a = Try { 2 }
        val e = RuntimeException()
        val f = { x: Int -> Success(2 * x + 1) }

        assertEquals(Success(5), a.flatMap(f))
        assertIs<Failure<RuntimeException>>(a.flatMap { Failure<Int>(e) })

        val b: Try<Int> = Try { throw e }
        val c: Try<Int> = b.flatMap { Failure(RuntimeException()) }
        assertSame(b, c)

        val d = b.flatMap(f)
        assertSame(d, b)
    }

    @Test
    fun conversions() {
        val a = Try { 2 }
        assertEquals(Right(2), a.toEither())
        assertEquals(Some(2), a.toOption())
        val e = RuntimeException()
        val b = Try { throw e }
        assertEquals(Left(e), b.toEither())
        assertEquals(None, b.toOption())
    }

    @Test
    fun fold() {
        val a = Try { 2 }
        val b = Try { -1 }
        val e = RuntimeException()
        val s = { x: Int -> if (x > 0) 2 * x + 1 else throw e }
        val f = { x: Throwable -> if (x === e) 0 else -1 }
        assertEquals(5, a.fold(s, f))
        assertEquals(0, b.fold(s, f))
        val c: Try<Int> = Failure(RuntimeException())
        assertEquals(-1, c.fold(s, f))
    }

    @Test
    fun transform() {
        val a = Try { 2 }
        val b = Try { -1 }
        val e1 = RuntimeException()
        val e2 = RuntimeException()
        val s = { x: Int -> Try { if (x > 0) 2 * x + 1 else throw e1 } }
        val f = { t: Throwable -> Try { if (t === e2) 0 else throw t } }

        assertEquals(Success(5), a.transform(s, f))
        assertEquals(Failure(e1), b.transform(s, f))

        val e3 = RuntimeException()
        assertEquals(Failure(e3), Failure<Int>(e3).transform(s, f))
        assertEquals(Success(0), Failure<Int>(e2).transform(s, f))
    }

    @Test
    fun filter() {
        val e = RuntimeException()
        val predicate = { x: Int -> if (x < 0) throw e else x % 2 == 0 }
        val a = Success(2)
        assertSame(a, a.filter(predicate))
        val x = Success(1).filter(predicate)
        assertIs<Failure<Int>>(x)
        assertIs<NoSuchElementException>(x.exception)
        assertEquals(Failure(e), Success(-1).filter(predicate))
        val b = Failure<Int>(RuntimeException())
        assertSame(b, b.filter(predicate))
    }

    @Test
    fun toTryBlock() {
        val x = "foobar".toTry { length }
        assertEquals(Success(6), x)
        val y = "foobar".toTry { throw IllegalArgumentException() }
        assertIs<Failure<IllegalArgumentException>>(y)
    }

    @Test
    fun toTry() {
        val x = "foobar".toTry()
        assertEquals(Success("foobar"), x)
        val e = IllegalArgumentException()
        val y = e.toTry()
        assertEquals(Failure(e), y)
    }

    @Test
    fun recover() {
        val x = Success(2)
        assertSame(x, x.recover { throw RuntimeException() })
        val y = Failure<Int>(RuntimeException())
        assertEquals(x, y.recover { x.value })
    }

    @Test
    fun flatRecover() {
        val x = Success(2)
        assertSame(x, x.flatRecover { throw RuntimeException() })
        val y = Failure<Int>(RuntimeException())
        assertSame(x, y.flatRecover { x })
    }
}
