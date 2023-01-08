package org.alc.utils


sealed interface Either<out A, out B> {
    fun <C> fold(leftFunction: (A) -> C, rightFunction: (B) -> C): C
    fun <C> foldLeft(c: C, rightFunction: (C, B) -> C): C
    fun swap(): Either<B, A>
    fun <B1> map(f: (B) -> B1): Either<A, B1>
    fun <A1> mapLeft(f: (A) -> A1): Either<A1, B>
    fun <U> forEach(f: (B) -> U)
    fun get(): B
    fun all(p: (B) -> Boolean): Boolean
    fun exists(p: (B) -> Boolean): Boolean
    fun toList(): List<B>
    fun toSet(): Set<B>
    fun toSequence(): Sequence<B>
    fun toOption(): Option<B>
    fun <U> onLeft(f: (A) -> U): Either<A, B>
    fun <U> onRight(f: (B) -> U): Either<A, B>
    fun getOrNull(): B?
}

/**
 * The left side of the disjoint union, as opposed to the [Right] side.
 */
data class Left<A>(internal val value: A) : Either<A, Nothing> {
    override fun <C> fold(leftFunction: (A) -> C, rightFunction: (Nothing) -> C) = leftFunction(value)
    override fun <C> foldLeft(c: C, rightFunction: (C, Nothing) -> C) = c
    override fun swap() = Right(value)
    override fun <B1> map(f: (Nothing) -> B1) = this
    override fun <A1> mapLeft(f: (A) -> A1) = Left(f(value))
    override fun <U> forEach(f: (Nothing) -> U) {}
    override fun get() = throw NoSuchElementException()
    override fun all(p: (Nothing) -> Boolean) = true
    override fun exists(p: (Nothing) -> Boolean) = false
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
data class Right<B>(internal val value: B) : Either<Nothing, B> {
    override fun <C> fold(leftFunction: (Nothing) -> C, rightFunction: (B) -> C) = rightFunction(value)
    override fun <C> foldLeft(c: C, rightFunction: (C, B) -> C) = rightFunction(c, value)
    override fun swap() = Left(value)
    override fun <B1> map(f: (B) -> B1) = flatMap { Right(f(value)) }
    override fun <A1> mapLeft(f: (Nothing) -> A1) = this

    override fun <U> forEach(f: (B) -> U) {
        f(value)
    }

    override fun get() = value
    override fun all(p: (B) -> Boolean) = p(value)
    override fun exists(p: (B) -> Boolean) = p(value)
    override fun toList() = listOf(value)
    override fun toSet() = setOf(value)
    override fun toSequence() = sequenceOf(value)
    override fun toOption() = Some(value)
    override fun <U> onLeft(f: (Nothing) -> U) = this
    override fun <U> onRight(f: (B) -> U) = also { f(it.value) }
    override fun getOrNull() = value
}

fun <A> A.toLeft(): Either<A, Nothing> = Left(this)
fun <A> A.toRight(): Either<Nothing, A> = Right(this)

fun <A, B> Either<A, B>.orElse(default: () -> Either<A, B>) = when (this) {
    is Right -> this
    else -> default()
}

fun <A, B> Either<A, B>.contains(b: B) = fold({ false }) { it == b }

fun <A, B, C> Either<A, B>.flatMap(f: (B) -> Either<A, C>): Either<A, C> = when (this) {
    is Right -> f(value)
    is Left -> this
}

fun <A, B> Either<A, B>.filterOrElse(p: (B) -> Boolean, default: () -> A) =
    flatMap { it.takeIf(p)?.toRight() ?: default().toLeft() }

fun <E : Exception, B> Either<E, B>.toTry(): Try<B> = fold({ Failure(it) }) { Success(it) }

fun <A, B, C> Either<A, B>.widen(): Either<A, C> = this as Either<A,C>

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
