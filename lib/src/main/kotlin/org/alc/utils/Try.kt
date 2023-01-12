package org.alc.utils

import java.util.*

sealed class Try<out T : Any> {
    companion object {
        operator fun <T : Any> invoke(f: () -> T): Try<T> =
            wrap { Success(f()) }
    }

    val isSuccess get() = this is Success
    val isFailure get() = !isSuccess

    fun get(): T = when (this) {
        is Success -> value
        is Failure -> throw exception
    }

    abstract fun <U : Any> map(f: (T) -> U): Try<U>
    abstract fun <U : Any> flatMap(f: (T) -> Try<U>): Try<U>

    abstract fun filter(predicate: (T) -> Boolean): Try<T>

    abstract fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>): Try<U>

    abstract fun <U> fold(s: (T) -> U, f: (Throwable) -> U): U

    abstract fun toEither(): Either<Exception, T>
    abstract fun toOption(): Option<T>
}

private fun <X : Any> wrap(block: () -> Try<X>) = try {
    block()
} catch (e: Exception) {
    Failure(e)
}

fun <T : Any, U : T> Try<T>.getOrElse(default: () -> U): T = when (this) {
    is Success -> value
    else -> default()
}

fun <T : Any, U : T> Try<T>.orElse(default: () -> Try<U>): Try<T> = when (this) {
    is Success -> this
    else -> wrap { default() }
}


data class Success<out T : Any>(val value: T) : Try<T>() {

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = try {
        s(value)
    } catch (e: Exception) {
        f(e)
    }

    override fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) = flatMap(s)

    override fun filter(predicate: (T) -> Boolean) = wrap {
        if (predicate(value)) this
        else Failure(NoSuchElementException("Predicate does not hold for $value"))
    }

    override fun <U : Any> flatMap(f: (T) -> Try<U>) = wrap { f(value) }
    override fun <U : Any> map(f: (T) -> U) = Try { f(value) }

    override fun toEither(): Either<Exception, T> = Right(value)
    override fun toOption() = Some(value)
}

data class Failure<T : Any>(val exception: Exception) : Try<T>() {

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = f(exception)

    override fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) =
        wrap { f(exception) }

    override fun filter(predicate: (T) -> Boolean) = this

    @Suppress("UNCHECKED_CAST")
    override fun <U : Any> flatMap(f: (T) -> Try<U>) = this as Failure<U>

    @Suppress("UNCHECKED_CAST")
    override fun <U : Any> map(f: (T) -> U) = this as Failure<U>

    override fun toEither() = Left(exception)
    override fun toOption() = None
}

fun <T : Any> Try<T>.recover(f: (Exception) -> T): Try<T> = when (this) {
    is Failure -> Try { f(exception) }
    else -> this
}

fun <T : Any> Try<T>.flatRecover(f: (Exception) -> Try<T>): Try<T> = when (this) {
    is Failure -> wrap { f(exception) }
    else -> this
}

fun <T, R : Any> T.toTry(block: T.() -> R) = Try { block() }
fun <T : Any> T.toTry() = when (this) {
    is Exception -> Failure(this)
    is Throwable -> throw this
    else -> Success(this)
}

