package org.alc.utils

import java.util.*

sealed class Try<out T> {
    companion object {
        operator fun <T> invoke(f: () -> T): Try<T> = try {
            Success(f())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    val isSuccess get() = this is Success
    val isFailure get() = !isSuccess

    fun get(): T = when (this) {
        is Success -> value
        is Failure -> throw exception
    }

    abstract fun <U> map(f: (T) -> U): Try<U>
    abstract fun <U> flatMap(f: (T) -> Try<U>): Try<U>

    abstract fun filter(predicate: (T) -> Boolean): Try<T>

    abstract fun <U> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>): Try<U>

    abstract fun <U> fold(s: (T) -> U, f: (Throwable) -> U): U

    fun toEither(): Either<Exception, T> = when (this) {
        is Success -> Right(value)
        is Failure -> Left(exception)
    }

    fun toOption(): Option<T> = when (this) {
        is Success -> Some(value)
        else -> None
    }
}

fun <T, U : T> Try<T>.getOrElse(default: () -> U): T = when (this) {
    is Success -> value
    else -> default()
}

fun <T, U : T> Try<T>.orElse(default: () -> Try<U>): Try<T> = when (this) {
    is Success -> this
    else -> try {
        default()
    } catch (e: Exception) {
        Failure(e)
    }
}


data class Success<out T>(val value: T) : Try<T>() {

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = try {
        s(value)
    } catch (e: Exception) {
        f(e)
    }

    override fun <U> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) = flatMap(s)

    override fun filter(predicate: (T) -> Boolean) = try {
        if (predicate(value)) this
        else Failure(NoSuchElementException("Predicate does not hold for $value"))
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U> flatMap(f: (T) -> Try<U>) = try {
        f(value)
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U> map(f: (T) -> U) = Try { f(value) }
}

data class Failure<T>(val exception: Exception) : Try<T>() {

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = f(exception)

    override fun <U> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) = try {
        f(exception)
    } catch (e: Exception) {
        Failure(e)
    }

    override fun filter(predicate: (T) -> Boolean) = this

    @Suppress("UNCHECKED_CAST")
    override fun <U> flatMap(f: (T) -> Try<U>) = this as Failure<U>

    @Suppress("UNCHECKED_CAST")
    override fun <U> map(f: (T) -> U) = this as Failure<U>
}

