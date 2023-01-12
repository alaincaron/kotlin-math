package org.alc.utils

import java.util.*

sealed class Try<out T : Any> {
    companion object {
        inline operator fun <T : Any> invoke(f: () -> T): Try<T> = try {
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

    abstract fun <U : Any> map(f: (T) -> U): Try<U>
    abstract fun <U : Any> flatMap(f: (T) -> Try<U>): Try<U>

    abstract fun filter(predicate: (T) -> Boolean): Try<T>

    abstract fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>): Try<U>

    abstract fun <U> fold(s: (T) -> U, f: (Throwable) -> U): U

    abstract fun toEither(): Either<Exception, T>
    abstract fun toOption(): Option<T>
}

fun <T : Any, U : T> Try<T>.getOrElse(default: () -> U): T = when (this) {
    is Success -> value
    else -> default()
}

fun <T : Any, U : T> Try<T>.orElse(default: () -> Try<U>): Try<T> = when (this) {
    is Success -> this
    else -> try {
        default()
    } catch (e: Exception) {
        Failure(e)
    }
}


data class Success<out T : Any>(val value: T) : Try<T>() {

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = try {
        s(value)
    } catch (e: Exception) {
        f(e)
    }

    override fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) = flatMap(s)

    override fun filter(predicate: (T) -> Boolean) = try {
        if (predicate(value)) this
        else Failure(NoSuchElementException("Predicate does not hold for $value"))
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U : Any> flatMap(f: (T) -> Try<U>) = try {
        f(value)
    } catch (e: Exception) {
        Failure(e)
    }

    override fun <U : Any> map(f: (T) -> U) = Try { f(value) }

    override fun toEither(): Either<Exception, T> = Right(value)

    override fun toOption() = Some(value)
}

data class Failure<T : Any>(val exception: Exception) : Try<T>() {

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = f(exception)

    override fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) = try {
        f(exception)
    } catch (e: Exception) {
        Failure(e)
    }

    override fun filter(predicate: (T) -> Boolean) = this

    @Suppress("UNCHECKED_CAST")
    override fun <U : Any> flatMap(f: (T) -> Try<U>) = this as Failure<U>

    @Suppress("UNCHECKED_CAST")
    override fun <U : Any> map(f: (T) -> U) = this as Failure<U>

    override fun toEither() = Left(exception)
    override fun toOption() = None
}

 fun <T, R: Any> T.toTry(block: T.() -> R) = Try { block() }
 fun <T: Any> T.toTry() = when (this) {
    is Exception -> Failure(this)
    is Throwable -> throw this
    else -> Success(this)
}

