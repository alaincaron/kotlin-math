package org.alc.utils

import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EitherTest {
    @Test
    fun right() {
        val a = Right(2)
        assertTrue(a.isRight())
        assertEquals(2, a.get())
        assertFalse(a.isLeft())
        assertEquals(2, a.getOrElse { 5 })
        assertEquals(Right(2), a.orElse { Right(5) })
    }

    @Test
    fun left() {
        val a = Left(2)
        assertFalse(a.isRight())
        assertTrue(a.isLeft())
        assertThrows<NoSuchElementException> { a.get() }
        assertEquals(2, a.swap().get())
        assertEquals(5, a.getOrElse { 5 })
        assertEquals(Right(5), a.orElse { Right(5) })
    }

    @Test
    fun fold() {
        assertEquals(
            2,
            Right(1).fold({ throw RuntimeException() }) { it + 1 }
        )
        assertEquals(
            1,
            Left(RuntimeException()).fold({ 1 }) { throw RuntimeException() }
        )
    }

    @Test
    fun foldLeft() {
        assertEquals(
            2,
            Right(1).foldLeft(1) { a, b -> a + b }
        )
        assertEquals(
            1,
            Left(2).foldLeft(1) { a: Int, b: Int -> a + b }
        )
    }

    @Test
    fun swap() {
        assertEquals(
            Right("left"),
            Left("left").swap()
        )
        assertEquals(
            Left("right"),
            Right("right").swap()
        )
    }

    @Test
    fun map() {
        assertEquals(
            Right(6),
            Right("flower").map { it.length }
        )
        assertEquals(
            Left(0),
            Left(0).map { throw RuntimeException() }
        )
    }

    @Test
    fun mapLeft() {
        assertEquals(
            Left(6),
            Left("flower").mapLeft { it.length }
        )
        assertEquals(
            Right(0),
            Right(0).mapLeft { throw RuntimeException() }
        )
    }

    @Test
    fun onRight() {
        var counter = 0
        val tapFunction = { _: Int -> counter += 1 }
        assertEquals(Right(2), Right(2).onRight(tapFunction))
        assertEquals(1, counter)
        assertEquals(Left(2), Left(2).onRight(tapFunction))
        assertEquals(1, counter)
    }

    @Test
    fun onLeft() {
        var counter = 0
        val tapFunction = { _: Int -> counter += 1 }
        assertEquals(Right(2), Right(2).onLeft(tapFunction))
        assertEquals(0, counter)
        assertEquals(Left(2), Left(2).onLeft(tapFunction))
        assertEquals(1, counter)
    }

    @Test
    fun exists() {
        assertTrue(Right(12).exists { it > 10 })
        assertFalse(Right(7).exists { it > 10 })

        val left: Either<Int, Int> = Left(12)
        assertFalse(left.exists { it > 10 })
    }

    @Test
    fun all() {
        assertTrue(Right(12).all { it > 10 })
        assertFalse(Right(7).all { it > 10 })

        val left: Either<Int, Int> = Left(12)
        assertTrue(left.all { it > 10 })
    }

    @Test
    fun forEach() {
        var counter = 0
        val f = { _: Int -> counter += 1 }
        Right(2).forEach(f)
        assertEquals(1, counter)
        Left(2).forEach(f)
        assertEquals(1, counter)
    }

    @Test
    fun flatten() {
        assertEquals(Right(5), Right(Right(5)).flatten())
        assertEquals(Left(5), Right(Left(5)).flatten())

        val a: Either<Int, Either<Int, Int>> = Left(5)
        val b: Either<Int, Int> = Left(5)
        assertEquals(b, a.flatten())
    }

    @Test
    fun merge() {
        assertEquals(12, Right(12).merge())
        assertEquals(12, Left(12).merge())
    }

    @Test
    fun filterOrElse() {
        assertEquals(Right(12), Right(12).filterOrElse({ it > 10 }, { 0 }))
        assertEquals(Left(0), Right(5).filterOrElse({ it > 10 }, { 0 }))
        val c: Either<Int, Int> = Left(23)
        assertEquals(Left(23), c.filterOrElse({ it > 10 }, { 0 }))
    }

    @Test
    fun widen() {
        val a: Either<Int, Int> = Right(5)
        val b: Either<Int, Number> = a.widen()
        assertEquals(5, b.get())

        val c: Either<Int, Int> = Left(2)
        val d: Either<Int, Number> = c.widen()
        assertEquals(2, d.fold({ it }, { it }))
    }

    @Test
    fun leftWiden() {
        val a: Either<Int, Int> = Right(5)
        val b: Either<Number, Int> = a.leftWiden()
        assertEquals(5, b.get())

        val c: Either<Int, Int> = Left(2)
        val d: Either<Number, Int> = c.leftWiden()
        assertEquals(2, d.fold({ it }, { it }))
    }

    @Test
    fun toTry() {
        assertEquals(Success(5), Right(5).toTry())
        val exc = RuntimeException()
        val left: Either<RuntimeException, Int> = exc.left()
        assertEquals(Failure(exc), left.toTry())
    }

    @Test
    fun toList() {
        assertEquals(emptyList(), Left(1).toList())
        assertEquals(listOf(1), Right(1).toList())
    }

    @Test
    fun toSet() {
        assertEquals(emptySet(), Left(1).toSet())
        assertEquals(setOf(1), Right(1).toSet())
    }

    @Test
    fun toSequence() {
        var counter = 0
        val f = { _: Int -> counter += 1 }
        Right(2).toSequence().forEach(f)
        assertEquals(1, counter)
        Left(2).toSequence().forEach(f)
        assertEquals(1, counter)
    }

    @Test
    fun joinRight() {
        val expectedRight = Right(12)
        val expectedLeft = Left("flower")

        assertEquals(
            expectedRight,
            Right(expectedRight).joinRight()
        )
        assertEquals(
            expectedLeft,
            Right(expectedLeft).joinRight()
        )

        assertEquals(
            expectedLeft,
            expectedLeft.joinRight()
        )
    }

    @Test
    fun joinLeft() {
        val v1: Either<Either<Int, String>, String> = Left(Right("flower"))
        val v2: Either<Either<Int, String>, String> = Left(Left(12))
        val v3: Either<Either<Int, String>, String> = Right("daisy")

        assertEquals(Right("flower"), v1.joinLeft())
        assertEquals(Left(12), v2.joinLeft())
        assertEquals(Right("daisy"), v3.joinLeft())
    }
}
