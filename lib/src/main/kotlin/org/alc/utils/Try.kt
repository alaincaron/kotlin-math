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

    val isSuccess: Boolean
    val isFailure: Boolean get() = !isSuccess

    fun <U : T> getOrElse(default: () -> U): T

    fun <U : T> orElse(default: () -> Try<U>): Try<T>

    fun get(): T

    fun <U> map(f: (t: T) -> U): Try<U>
    fun <U> flatMap(f: (t: T) -> Try<U>): Try<U>

    fun filter(predicate: (t: T) -> Boolean): Try<T>

    fun <U> transform(s: (t: T) -> Try<U>, f: (t: Throwable) -> Try<U>): Try<U>

    fun <U> fold(s: (t: T) -> U, f:(t: Throwable) -> U): U
}

data class Success<T>(val value: T) : Try<T> {
    override val isSuccess = true

    override fun <U : T> getOrElse(default: () -> U) = value

    override fun <U : T> orElse(default: () -> Try<U>) = this

    override fun get() = value

    override fun <U> fold(s: (t: T) -> U, f: (t: Throwable) -> U) = try {
        s(value)
    } catch (e: Exception) {
        f(e)
    }

    override fun <U> transform(s: (t: T) -> Try<U>, f: (t:Throwable) -> Try<U>) = flatMap(s)

    override fun filter(predicate: (t: T) -> Boolean) = try {
        if (predicate(value)) this
        else Failure(NoSuchElementException("Predicate does not hold for $value"))
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U> flatMap(f: (t: T) -> Try<U>) = try {
        f(value)
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U> map(f: (t: T) ->  U) = Try { f(value) }
}

data class Failure<T>(val exception: Exception) : Try<T> {
    override val isSuccess = false
    override fun get() = throw exception

    override fun <U> fold(s: (t: T) -> U, f: (t: Throwable) -> U) = f(exception)

    override fun <U> transform(s: (t: T) -> Try<U>, f: (t: Throwable) -> Try<U>) = try {
        f(exception)
    } catch (e: Exception) {
        Failure(e)
    }

    override fun filter(predicate: (t: T) -> Boolean) = this

    @Suppress("UNCHECKED_CAST")
    override fun <U> flatMap(f: (t: T) -> Try<U>) = this as Failure<U>

    @Suppress("UNCHECKED_CAST")
    override fun <U> map(f: (t: T) -> U) = this as Failure<U>

    @Suppress("UNCHECKED_CAST")
    override fun <U : T> orElse(default: () -> Try<U>) = try {
        default() as Try<T>
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U : T> getOrElse(default: () -> U) = default()
}

