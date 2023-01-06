package org.alc.utils

sealed class Option<out A> {

    companion object {

        @JvmStatic
        operator fun <A> invoke(a: A?): Option<A> = if (a != null) Some(a) else None

        @JvmStatic
        fun <A> empty(): Option<A> = None

        @JvmStatic
        fun <A> unless(cond: Boolean, block: () -> A) =
            if (cond) None else Some(block())

        @JvmStatic
        fun <A> createIf(cond: Boolean, block: () -> A) =
            if (cond) Some(block) else None
    }

    fun <U> onNone(block: () -> U): Option<A> {
        if (this is None) block()
        return this
    }

    fun <U> onSome(block: (A) -> U): Option<A> {
        if (this is Some) block(value)
        return this
    }

    fun isEmpty(): Boolean = this is None

    fun isNotEmpty(): Boolean = !isEmpty()

    fun isDefined(): Boolean = !isEmpty()

    fun get(): A = fold({ throw NoSuchElementException() }) { it }

    fun orNull(): A? = fold({ null }, { it })

    fun <B> fold(ifEmpty: () -> B, ifSome: (A) -> B): B = when (this) {
        is None -> ifEmpty()
        is Some -> ifSome(value)
    }

    fun <B> map(f: (A) -> B?): Option<B> = fold({ None }) { Option(f(it)) }

    fun <B> flatMap(f: (A) -> Option<B>): Option<B> = fold({ None }) { f(it) }


    fun all(predicate: (A) -> Boolean): Boolean = fold({ true }, predicate)

    fun filter(predicate: (A) -> Boolean): Option<A> =
        map { if (predicate(it)) it else null }

    fun filterNot(predicate: (A) -> Boolean): Option<A> =
        map { if (!predicate(it)) it else null }

    fun exists(predicate: (A) -> Boolean): Boolean = fold({ false }, predicate)

    fun <B> foldLeft(initial: B, operation: (B, A) -> B): B =
        fold({ initial }, { operation(initial, it) })

    fun <L> toEither(ifEmpty: () -> L): Either<L, A> =
        fold({ ifEmpty().left() }, { it.right() })

    fun toList(): List<A> = fold(::emptyList) { listOf(it) }
    fun toSet(): Set<A> = fold(::emptySet) { setOf(it) }
    fun toSequence(): Sequence<A> = fold(::emptySequence) { sequenceOf(it) }
}

object None : Option<Nothing>() {
    override fun toString(): String = "Option.None"
}

data class Some<out T>(val value: T) : Option<T>() {
    override fun toString(): String = "Option.Some($value)"
}

fun <A> Option<A>.getOrElse(default: () -> A): A = fold({ default() }) { it }

fun <A> Option<A>.orElse(default: () -> Option<A>): Option<A> =
    if (isEmpty()) default() else this

fun <T> T?.toOption(): Option<T> = this?.let { Some(it) } ?: None

inline fun <A> Boolean.maybe(f: () -> A): Option<A> =
    if (this) {
        Some(f())
    } else {
        None
    }

public fun <A> A.some(): Option<A> = Some(this)
public fun <A> A.none(): Option<A> = None

public fun <A> Option<Option<A>>.flatten(): Option<A> = flatMap { it }


public fun <B, A : B> Option<A>.widen(): Option<B> = this

