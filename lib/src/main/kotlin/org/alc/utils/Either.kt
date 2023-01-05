package org.alc.utils

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


sealed class Either<out A, out B> {

    @OptIn(ExperimentalContracts::class)
    fun isLeft(): Boolean {
        contract { returns(true) implies (this@Either is Left<A>) }
        return this@Either is Left<A>
    }

    @OptIn(ExperimentalContracts::class)
    fun isRight(): Boolean {
        contract { returns(true) implies (this@Either is Right<B>) }
        return this@Either is Right<B>
    }

    fun <C> fold(leftFunction: (a: A) -> C, rightFunction: (b: B) -> C) =
        when (this) {
            is Right -> rightFunction(value)
            is Left -> leftFunction(value)
        }


    fun <C> foldLeft(c: C, rightFunction: (c: C, b: B) -> C) =
        fold({ c }, { rightFunction(c, it) })

    fun swap(): Either<B,A> = when (this) {
        is Left -> Right(value)
        is Right -> Left(value)
    }

    fun <B1> map(f: (b: B) -> B1) = flatMap { Right(f(it)) }

    fun <A1> mapLeft(f: (A) -> A1): Either<A1, B> = fold({ Left(f(it)) }, { Right(it) })

    fun <U> forEach(f: (b: B) -> U) {
        if (this is Right) f(value)
    }

    fun get() = when (this) {
        is Right -> value
        else -> throw NoSuchElementException()
    }


    fun all(p: (b: B) -> Boolean) = fold({true}, p)

    fun exists(p: (b: B) -> Boolean) = fold({false}, p)


    fun toList() = when (this) {
        is Right -> listOf(value)
        else -> emptyList()
    }

    fun toSet() = when (this) {
        is Right -> setOf(value)
        else -> emptySet()
    }



    fun <U> onLeft(f: (a: A) -> U) = also { if (it.isLeft()) f(it.value) }
    fun <U> onRight(f: (b: B) -> U) = also { if (it.isRight()) f(it.value) }

    fun getOrNull(): B? = when (this) {
        is Right -> value
        else -> null
    }
}

/**
 * The left side of the disjoint union, as opposed to the [Right] side.
 */
data class Left<A>(internal val value: A) : Either<A, Nothing>()


/**
 * The right side of the disjoint union, as opposed to the [Left] side.
 */
data class Right<B>(internal val value: B) : Either<Nothing, B>()

fun <A> A.left(): Either<A, Nothing> = Left(this)

fun <A> A.right(): Either<Nothing, A> = Right(this)

fun <A, B> Either<A, B>.orElse(default: () -> Either<A, B>) = when (this) {
    is Right -> this
    else -> default()
}

fun <A, B> Either<A, B>.contains(b: B) = when(this) {
    is Right -> value == b
    else -> false
}

fun <A, B, C> Either<A, B>.flatMap(f: (right: B) -> Either<A, C>): Either<A, C> =
    when (this) {
        is Right -> f(value)
        is Left -> this
    }

fun <A,B> Either<A,B>.filterOrElse(p: (b: B) -> Boolean, default: () -> A) =
    flatMap { b -> b.takeIf(p)?.right() ?: default().left() }

fun <E: Exception, B>Either<E, B>.toTry(): Try<B> = when (this) {
    is Right -> Success(value)
    is Left -> Failure(value)
}
fun <A, C, B : C> Either<A, B>.widen(): Either<A, C> = this

fun <AA, A : AA, B> Either<A, B>.leftWiden(): Either<AA, B> = this

fun <A> Either<A, A>.merge(): A = when (this) {
    is Right -> value
    is Left -> value
}


fun <A, B> Either<A, Either<A, B>>.flatten(): Either<A, B> = flatMap { it }
fun <B> Either<*, B>.getOrElse(default: () -> B): B = fold({ default() }, { it })

