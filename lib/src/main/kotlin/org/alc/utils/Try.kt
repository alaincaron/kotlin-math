package org.alc.utils

import java.util.*

sealed interface Try<T> {
    companion object {
        operator fun <T> invoke(f: () -> T): Try<T> = try {
            Success(f())
        } catch (e: Exception) {
            Failure(e)
        }
    }

    val isSuccess get() = this is Success
    val isFailure get() = !isSuccess

    fun <U : T> getOrElse(default: () -> U): T

    fun <U : T> orElse(default: () -> Try<U>): Try<T>

    fun get(): T

    fun <U> map(f: (T) -> U): Try<U>
    fun <U> flatMap(f: (T) -> Try<U>): Try<U>

    fun filter(predicate: (T) -> Boolean): Try<T>

    fun <U> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>): Try<U>

    fun <U> fold(s: (T) -> U, f: (Throwable) -> U): U

    fun toEither(): Either<Exception,T>
    fun toOption(): Option<T>
}

data class Success<T>(val value: T) : Try<T> {

    override fun <U : T> getOrElse(default: () -> U) = value

    override fun <U : T> orElse(default: () -> Try<U>) = this

    override fun get() = value

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

    override fun toEither(): Either<Exception, T> = Right(value)
    override fun toOption(): Option<T> = Some(value)
}

data class Failure<T>(val exception: Exception) : Try<T> {
    override fun get() = throw exception

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

    @Suppress("UNCHECKED_CAST")
    override fun <U : T> orElse(default: () -> Try<U>) = try {
        default() as Try<T>
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U : T> getOrElse(default: () -> U) = default()
    override fun toEither(): Either<Exception, T> = Left(exception)
    override fun toOption(): Option<T> = None
}

