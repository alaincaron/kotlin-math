package org.alc.utils



sealed class Either<out A, out B> {
    fun <C> fold(leftFunction: (A) -> C, rightFunction: (B) -> C) = when (this) {
        is Right -> rightFunction(value)
        is Left -> leftFunction(value)
    }

    fun <C> foldLeft(c: C, rightFunction: (C, B) -> C) =
        fold({ c }, { rightFunction(c, it) })

    fun swap(): Either<B, A> = fold({ Right(it) }) { Left(it) }

    fun <B1> map(f: (B) -> B1) = flatMap { Right(f(it)) }

    fun <A1> mapLeft(f: (A) -> A1): Either<A1, B> = fold({ Left(f(it)) }, { Right(it) })

    fun <U> forEach(f: (B) -> U) {
        if (this is Right) f(value)
    }

    fun get() = fold({ throw NoSuchElementException() }) { it }

    fun all(p: (B) -> Boolean) = fold({ true }, p)

    fun exists(p: (B) -> Boolean) = fold({ false }, p)

    fun toList() = fold({ emptyList() }) { listOf(it) }

    fun toSet() = fold({ emptySet() }) { setOf(it) }

    fun toSequence() = fold({ emptySequence() }) { sequenceOf(it) }

    fun toOption(): Option<B> = fold({ None }, { Option(it) })

    fun <U> onLeft(f: (A) -> U) = also { if (it is Left) f(it.value) }
    fun <U> onRight(f: (B) -> U) = also { if (it is Right) f(it.value) }

    fun getOrNull(): B? = fold({ null }) { it }
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

fun <A, B> Either<A, B>.orElse(default: () -> Either<A, B>) =
    when (this) {
        is Right -> this
        else -> default()
    }

fun <A, B> Either<A, B>.contains(b: B) = fold({ false }) { it == b }

fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> =
    when (this) {
        is Right -> f(value)
        is Left -> this
    }

fun <A, B> Either<A, B>.filterOrElse(p: (B) -> Boolean, default: () -> A) =
    flatMap { it.takeIf(p)?.right() ?: default().left() }

fun <E : Exception, B> Either<E, B>.toTry(): Try<B> = fold({ Failure(it) }) { Success(it) }

fun <A, C, B : C> Either<A, B>.widen(): Either<A, C> = this

fun <AA, A : AA, B> Either<A, B>.leftWiden(): Either<AA, B> = this

fun <A> Either<A, A>.merge(): A = fold({ it }) { it }

fun <A, B> Either<A, Either<A, B>>.flatten(): Either<A, B> = flatMap { it }
fun <B> Either<*, B>.getOrElse(default: () -> B): B = fold({ default() }, { it })

@Suppress("UNCHECKED_CAST")
fun <A, B, A1 : A> Either<A, Either<A1, B>>.joinRight() = when (this) {
    is Right -> this.value
    else -> this as Either<A, B>
}

@Suppress("UNCHECKED_CAST")
fun <A, B, A1 : A, B1 : B> Either<Either<A1, B1>, B>.joinLeft() = when (this) {
    is Left -> this.value
    else -> this as Either<A, B>
}
