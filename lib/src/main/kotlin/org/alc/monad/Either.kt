package org.alc.monad


sealed interface EitherMonad<out A : Any, out B : Any> : Monad<A, B> {
    fun filter(predicate: (B) -> Boolean): Option<EitherMonad<A, B>>
    override fun <B1 : Any> map(f: (B) -> B1): EitherMonad<A, B1>
    fun <C> fold(leftFunction: (A) -> C, rightFunction: (B) -> C): C
}

sealed interface Either<out A : Any, out B : Any> : EitherMonad<A, B> {
    fun swap(): Either<B, A>
    fun <U> onLeft(f: (A) -> U): Either<A, B>
    fun <U> onRight(f: (B) -> U): Either<A, B>
    fun <C> foldLeft(c: C, rightFunction: (C, B) -> C): C
    fun <A1 : Any> mapLeft(f: (A) -> A1): Either<A1, B>
}


/**
 * The left side of the disjoint union, as opposed to the [Right] side.
 */
data class Left<A : Any>(internal val value: A) : Either<A, Nothing> {
    override fun <C> fold(leftFunction: (A) -> C, rightFunction: (Nothing) -> C) = leftFunction(value)
    override fun <C> foldLeft(c: C, rightFunction: (C, Nothing) -> C) = c
    override fun swap() = Right(value)
    override fun <B1 : Any> map(f: (Nothing) -> B1) = this
    override fun <A1 : Any> mapLeft(f: (A) -> A1) = Left(f(value))
    override fun <U> forEach(f: (Nothing) -> U) {}
    override fun filter(predicate: (Nothing) -> Boolean) = None
    override fun get() = throw NoSuchElementException()
    override fun all(predicate: (Nothing) -> Boolean) = true
    override fun exists(predicate: (Nothing) -> Boolean) = false
    override fun toList() = emptyList<Nothing>()
    override fun toSet() = emptySet<Nothing>()
    override fun toSequence() = emptySequence<Nothing>()
    override fun toOption() = None
    override fun <U> onLeft(f: (A) -> U) = also { f(it.value) }
    override fun <U> onRight(f: (Nothing) -> U) = this
    override fun getOrNull(): Nothing? = null
}

/**
 * The right side of the disjoint union, as opposed to the [Left] side.
 */
data class Right<B : Any>(internal val value: B) : Either<Nothing, B> {
    override fun <C> fold(leftFunction: (Nothing) -> C, rightFunction: (B) -> C) = rightFunction(value)
    override fun <C> foldLeft(c: C, rightFunction: (C, B) -> C) = rightFunction(c, value)
    override fun swap() = Left(value)
    override fun <B1 : Any> map(f: (B) -> B1) = flatMap { Right(f(value)) }
    override fun <A1 : Any> mapLeft(f: (Nothing) -> A1) = this

    override fun <U> forEach(f: (B) -> U) {
        f(value)
    }

    override fun filter(predicate: (B) -> Boolean) = predicate(value).maybe { this }

    override fun get() = value
    override fun all(predicate: (B) -> Boolean) = predicate(value)
    override fun exists(predicate: (B) -> Boolean) = predicate(value)
    override fun toList() = listOf(value)
    override fun toSet() = setOf(value)
    override fun toSequence() = sequenceOf(value)
    override fun toOption() = Some(value)
    override fun <U> onLeft(f: (Nothing) -> U) = this
    override fun <U> onRight(f: (B) -> U) = also { f(it.value) }
    override fun getOrNull() = value
}

class LeftProjection<A : Any, B : Any> internal constructor(e: Either<A, B>) : EitherMonad<B, A> {
    private val entry = e.swap()
    override fun filter(predicate: (A) -> Boolean): Option<LeftProjection<A,B>> =
        if (entry.exists(predicate)) Some(this) else None

    override fun <A1 : Any> map(f: (A) -> A1): EitherMonad<B, A1> = entry.map(f)

    override fun <C> fold(leftFunction: (B) -> C, rightFunction: (A) -> C) =
        entry.fold(leftFunction, rightFunction)

    override fun exists(predicate: (A) -> Boolean) = entry.exists(predicate)

    override fun <U> forEach(f: (A) -> U) = entry.forEach(f)

    override fun all(predicate: (A) -> Boolean) = entry.all(predicate)

    override fun get() = entry.get()

    override fun toList() = entry.toList()

    override fun toSet() = entry.toSet()

    override fun toSequence() = entry.toSequence()

    override fun toOption() = entry.toOption()

    override fun getOrNull() = entry.getOrNull()
}

fun <A: Any, B: Any> Either<A,B>.leftProjection() = LeftProjection(this)

fun <A : Any> A.toLeft(): Either<A, Nothing> = Left(this)
fun <A : Any> A.toRight(): Either<Nothing, A> = Right(this)

fun <A : Any, B : Any> Either<A, B>.orElse(default: () -> Either<A, B>) = when (this) {
    is Right -> this
    else -> default()
}

fun <A : Any, B : Any> Either<A, B>.contains(b: B) = fold({ false }) { it == b }

fun <A : Any, B : Any, C : Any> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> = when (this) {
    is Right -> f(value)
    is Left -> this
}

fun <A : Any, B : Any, C : Any> Either<A, B>.flatMapLeft(f: (A) -> Either<C, B>): Either<C, B> = when (this) {
    is Right -> this
    is Left -> f(value)
}

fun <A : Any, B : Any> Either<A, B>.filterOrElse(p: (B) -> Boolean, default: () -> A) =
    flatMap { it.takeIf(p)?.toRight() ?: default().toLeft() }

fun <E : Exception, B : Any> Either<E, B>.toTry(): Try<B> = fold({ Failure(it) }) { Success(it) }

@Suppress("UNCHECKED_CAST")
fun <A : Any, B : Any, C : Any> Either<A, B>.widen(): Either<A, C> = this as Either<A, C>

fun <AA : Any, A : AA, B : Any> Either<A, B>.leftWiden(): Either<AA, B> = this

fun <A : Any> Either<A, A>.merge(): A = fold({ it }) { it }

fun <A : Any, B : Any> Either<A, Either<A, B>>.flatten(): Either<A, B> = flatMap { it }
fun <B : Any> Either<*, B>.getOrElse(default: () -> B): B = fold({ default() }, { it })

@Suppress("UNCHECKED_CAST")
fun <A : Any, B : Any, A1 : A> Either<A, Either<A1, B>>.joinRight() = when (this) {
    is Right -> this.value
    else -> this as Either<A, B>
}

@Suppress("UNCHECKED_CAST")
fun <A : Any, B : Any, A1 : A, B1 : B> Either<Either<A1, B1>, B>.joinLeft() = when (this) {
    is Left -> this.value
    else -> this as Either<A, B>
}
