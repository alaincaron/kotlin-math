package org.alc.monad

sealed class Try<out T : Any> : Monad<Throwable, T> {
    companion object {
        operator fun <T : Any> invoke(f: () -> T): Try<T> =
            wrap { Success(f()) }
    }

    abstract val isSuccess: Boolean
    val isFailure get() = !isSuccess

    abstract override fun <U : Any> map(f: (T) -> U): Try<U>
    abstract fun <U : Any> flatMap(f: (T) -> Try<U>): Try<U>
    abstract fun filter(predicate: (T) -> Boolean): Try<T>
    abstract fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>): Try<U>
    abstract fun <U> fold(s: (T) -> U, f: (Throwable) -> U): U
    abstract fun toEither(): Either<Throwable, T>
    abstract override fun toOption(): Option<T>
}

private fun <X : Any> wrap(block: () -> Try<X>) = try {
    block()
} catch (e: Throwable) {
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
    override val isSuccess = true
    override fun get() = value

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = try {
        s(value)
    } catch (e: Throwable) {
        f(e)
    }

    override fun filter(predicate: (T) -> Boolean) = wrap {
        if (predicate(value)) this
        else Failure(NoSuchElementException("Predicate does not hold for $value"))
    }

    override fun <U : Any> flatMap(f: (T) -> Try<U>) = wrap { f(value) }
    override fun <U : Any> map(f: (T) -> U) = Try { f(value) }
    override fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) = flatMap(s)

    override fun exists(predicate: (T) -> Boolean) = predicate(value)
    override fun <U> forEach(f: (T) -> U) {
        f(value)
    }

    override fun all(predicate: (T) -> Boolean) = predicate(value)
    override fun toList() = listOf(value)
    override fun toSet() = setOf(value)
    override fun toSequence() = sequenceOf(value)
    override fun getOrNull() = value
    override fun toEither(): Either<Throwable, T> = Right(value)
    override fun toOption() = Some(value)
}

data class Failure<T : Any>(val exception: Throwable) : Try<T>() {
    override val isSuccess = false
    override fun get() = throw exception

    override fun <U> fold(s: (T) -> U, f: (Throwable) -> U) = f(exception)

    override fun filter(predicate: (T) -> Boolean) = this

    @Suppress("UNCHECKED_CAST")
    override fun <U : Any> flatMap(f: (T) -> Try<U>) = this as Failure<U>

    @Suppress("UNCHECKED_CAST")
    override fun <U : Any> map(f: (T) -> U) = this as Failure<U>

    override fun <U : Any> transform(s: (T) -> Try<U>, f: (Throwable) -> Try<U>) =
        wrap { f(exception) }

    override fun exists(predicate: (T) -> Boolean) = false
    override fun <U> forEach(f: (T) -> U) = Unit
    override fun all(predicate: (T) -> Boolean) = true
    override fun toList() = emptyList<T>()
    override fun toSet() = emptySet<T>()
    override fun toSequence() = emptySequence<T>()
    override fun getOrNull() = null
    override fun toEither() = Left(exception)
    override fun toOption() = None
}

fun <T : Any> Try<T>.recover(f: (Throwable) -> T): Try<T> = when (this) {
    is Failure -> Try { f(exception) }
    else -> this
}

fun <T : Any> Try<T>.flatRecover(f: (Throwable) -> Try<T>): Try<T> = when (this) {
    is Failure -> wrap { f(exception) }
    else -> this
}

fun <T, R : Any> T.toTry(block: T.() -> R) = Try { block() }
fun <T : Any> T.toTry() = when (this) {
    is Throwable -> Failure(this)
    else -> Success(this)
}

