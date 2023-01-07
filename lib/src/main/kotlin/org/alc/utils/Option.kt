package org.alc.utils

sealed class Option<out A> {

    companion object {

        @JvmStatic
        operator fun <A> invoke(a: A) = Some(a)

        @JvmStatic
        fun <A> empty(): Option<A> = None

        @JvmStatic
        fun <A> ofNullable(a: A?): Option<A> = if (a != null) Some(a) else None

        @JvmStatic
        fun <A> unless(cond: Boolean, block: () -> A) =
            if (cond) None else Some(block())

        @JvmStatic
        fun <A> createIf(cond: Boolean, block: () -> A) =
            if (cond) Some(block()) else None
    }

    abstract fun <U> onNone(block: () -> U): Option<A>
    abstract fun <U> onSome(block: (A) -> U): Option<A>
    abstract fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean = !isEmpty()
    fun isDefined(): Boolean = !isEmpty()
    abstract fun get(): A
    abstract fun getOrNull(): A?
    abstract fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B
    abstract fun <B> map(f: (A) -> B?): Option<B>
    abstract fun <B> flatMap(f: (A) -> Option<B>): Option<B>
    abstract fun all(predicate: (A) -> Boolean): Boolean
    abstract fun filter(predicate: (A) -> Boolean): Option<A>
    abstract fun filterNot(predicate: (A) -> Boolean): Option<A>
    abstract fun exists(predicate: (A) -> Boolean): Boolean
    abstract fun <B> foldLeft(initial: B, operation: (B, A) -> B): B
    abstract fun <L> toEither(ifEmpty: () -> L): Either<L, A>
    abstract fun toList(): List<A>
    abstract fun toSet(): Set<A>
    abstract fun toSequence(): Sequence<A>
}

object None : Option<Nothing>() {
    override fun isEmpty() = true
    override fun get() = throw NoSuchElementException()
    override fun getOrNull() = null
    override fun <B> fold(ifEmpty: () -> B, ifSome: (Nothing) -> B) = ifEmpty()
    override fun <B> map(f: (Nothing) -> B?) = this
    override fun <B> flatMap(f: (Nothing) -> Option<B>) = this
    override fun all(predicate: (Nothing) -> Boolean) = true
    override fun filter(predicate: (Nothing) -> Boolean) = this
    override fun filterNot(predicate: (Nothing) -> Boolean) = this
    override fun exists(predicate: (Nothing) -> Boolean) = false
    override fun <B> foldLeft(initial: B, operation: (B, Nothing) -> B) = initial
    override fun <L> toEither(ifEmpty: () -> L): Either<L, Nothing> = ifEmpty().left()
    override fun toList() = emptyList<Nothing>()
    override fun toSet() = emptySet<Nothing>()
    override fun toSequence() = emptySequence<Nothing>()

    override fun <U> onNone(block: () -> U): Option<Nothing> {
        block()
        return this
    }

    override fun <U> onSome(block: (Nothing) -> U): Option<Nothing> {
        return this
    }

    override fun toString(): String = "Option.None"
}

data class Some<out A>(val value: A) : Option<A>() {
    override fun isEmpty() = false
    override fun get() = value
    override fun getOrNull() = value
    override fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B) = ifSome(value)
    override fun <B> map(f: (A) -> B?) = ofNullable(f(value))
    override fun <B> flatMap(f: (A) -> Option<B>) = f(value)
    override fun all(predicate: (A) -> Boolean) = predicate(value)
    override fun filter(predicate: (A) -> Boolean) = map { if (predicate(it)) it else null }
    override fun filterNot(predicate: (A) -> Boolean) = map { if (!predicate(it)) it else null }
    override fun exists(predicate: (A) -> Boolean): Boolean = predicate(value)
    override fun <B> foldLeft(initial: B, operation: (B, A) -> B) = operation(initial, value)
    override fun <L> toEither(ifEmpty: () -> L): Either<L, A> = Right(value)
    override fun toList() = listOf(value)
    override fun toSet() = setOf(value)
    override fun toSequence() = sequenceOf(value)

    override fun <U> onNone(block: () -> U) = this

    override fun <U> onSome(block: (A) -> U): Option<A> {
        block(value)
        return this
    }

    override fun toString(): String = "Option.Some($value)"
}

fun <A> Option<A>.getOrElse(default: () -> A): A = fold({ default() }) { it }

fun <A> Option<A>.orElse(default: () -> Option<A>): Option<A> =
    if (isEmpty()) default() else this

fun <T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

fun <A> Boolean.maybe(f: () -> A): Option<A> =
    if (this) {
        Some(f())
    } else {
        None
    }

fun <A> A.asSome(): Option<A> = Some(this)
fun <A> A.asNone(): Option<A> = None

fun <A> Option<Option<A>>.flatten(): Option<A> = flatMap { it }


fun <B, A : B> Option<A>.widen(): Option<B> = this

