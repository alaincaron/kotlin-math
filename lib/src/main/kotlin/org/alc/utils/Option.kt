package org.alc.utils

sealed interface Option<out A:Any> {

    companion object {

        @JvmStatic
        operator fun <A:Any> invoke(a: A) = Some(a)

        @JvmStatic
        fun <A:Any> empty(): Option<A> = None

        @JvmStatic
        fun <A:Any> ofNullable(a: A?): Option<A> = if (a != null) Some(a) else None

        @JvmStatic
        fun <A:Any> unless(cond: Boolean, block: () -> A) =
            if (cond) None else Some(block())

        @JvmStatic
        fun <A:Any> createIf(cond: Boolean, block: () -> A) =
            if (cond) Some(block()) else None
    }

    fun <U> onNone(block: () -> U): Option<A>
    fun <U> onSome(block: (A) -> U): Option<A>
    fun isEmpty(): Boolean
    fun isNotEmpty(): Boolean = !isEmpty()
    fun isDefined(): Boolean = !isEmpty()
    fun get(): A
    fun getOrNull(): A?
    fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B
    fun <B:Any> map(f: (A) -> B?): Option<B>
    fun <B:Any> flatMap(f: (A) -> Option<B>): Option<B>
    fun all(predicate: (A) -> Boolean): Boolean
    fun filter(predicate: (A) -> Boolean): Option<A>
    fun filterNot(predicate: (A) -> Boolean): Option<A>
    fun exists(predicate: (A) -> Boolean): Boolean
    fun <B> foldLeft(initial: B, operation: (B, A) -> B): B
    fun <L:Any> toEither(ifEmpty: () -> L): Either<L, A>
    fun toList(): List<A>
    fun toSet(): Set<A>
    fun toSequence(): Sequence<A>
}

object None : Option<Nothing> {
    override fun isEmpty() = true
    override fun get() = throw NoSuchElementException()
    override fun getOrNull() = null
    override fun <B> fold(ifEmpty: () -> B, ifSome: (Nothing) -> B) = ifEmpty()
    override fun <B:Any> map(f: (Nothing) -> B?) = this
    override fun <B:Any> flatMap(f: (Nothing) -> Option<B>) = this
    override fun all(predicate: (Nothing) -> Boolean) = true
    override fun filter(predicate: (Nothing) -> Boolean) = this
    override fun filterNot(predicate: (Nothing) -> Boolean) = this
    override fun exists(predicate: (Nothing) -> Boolean) = false
    override fun <B> foldLeft(initial: B, operation: (B, Nothing) -> B) = initial
    override fun <L:Any> toEither(ifEmpty: () -> L): Either<L, Nothing> = ifEmpty().toLeft()
    override fun toList() = emptyList<Nothing>()
    override fun toSet() = emptySet<Nothing>()
    override fun toSequence() = emptySequence<Nothing>()
    override fun <U> onNone(block: () -> U): Option<Nothing> = also { block() }
    override fun <U> onSome(block: (Nothing) -> U): Option<Nothing> = this
    override fun toString(): String = "Option.None"
}

data class Some<out A:Any>(val value: A) : Option<A> {
    override fun isEmpty() = false
    override fun get() = value
    override fun getOrNull() = value
    override fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B) = ifSome(value)
    override fun <B:Any> map(f: (A) -> B?) = Option.ofNullable(f(value))
    override fun <B:Any> flatMap(f: (A) -> Option<B>) = f(value)
    override fun all(predicate: (A) -> Boolean) = predicate(value)
    override fun filter(predicate: (A) -> Boolean) = map { if (predicate(it)) it else null }
    override fun filterNot(predicate: (A) -> Boolean) = map { if (!predicate(it)) it else null }
    override fun exists(predicate: (A) -> Boolean): Boolean = predicate(value)
    override fun <B> foldLeft(initial: B, operation: (B, A) -> B) = operation(initial, value)
    override fun <L:Any> toEither(ifEmpty: () -> L): Either<L, A> = Right(value)
    override fun toList() = listOf(value)
    override fun toSet() = setOf(value)
    override fun toSequence() = sequenceOf(value)
    override fun <U> onNone(block: () -> U) = this
    override fun <U> onSome(block: (A) -> U) = also { block(value) }
    override fun toString(): String = "Option.Some($value)"
}

fun <A:Any> Option<A>.getOrElse(default: () -> A): A = fold({ default() }) { it }

fun <A:Any> Option<A>.orElse(default: () -> Option<A>): Option<A> =
    if (isEmpty()) default() else this

fun <T:Any> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

fun <A:Any> Boolean.maybe(f: () -> A): Option<A> =
    if (this) {
        Some(f())
    } else {
        None
    }

fun <A:Any> A.toSome(): Option<A> = Some(this)
fun <A:Any> A?.toNone(): Option<A> = None

fun <A:Any> Option<Option<A>>.flatten(): Option<A> = flatMap { it }

fun <B:Any, A : B> Option<A>.widen(): Option<B> = this

