package org.alc.math.vector

import org.alc.math.ring.RingElement

fun requireSameSize(a: Array<*>, b: Array<*>) {
    require(a.size == b.size) { "Arrays must be of same size" }
}

inline operator fun <reified T : RingElement<T>> Array<T>.plus(other: Array<T>): Array<T> {
    requireSameSize(this, other)
    return Array(size) { i -> this[i] + other[i] }
}

inline operator fun <reified T : RingElement<T>> Array<T>.minus(other: Array<T>): Array<T> {
    requireSameSize(this, other)
    return Array(size) { i -> this[i] - other[i] }
}

operator fun <T : RingElement<T>> Array<T>.times(other: Array<T>): T {
    requireSameSize(this, other)
    require(isNotEmpty())
    var sum = this[0] * other[0]
    for (i in 1 until size) sum += this[i] * other[i]
    return sum
}

inline operator fun <reified T : RingElement<T>> Array<T>.times(other: T): Array<T> {
    return Array(size) { i -> this[i] * other }
}

inline operator fun <reified T : RingElement<T>> T.times(other: Array<T>) =
    Array(other.size) { i -> this * other[i] }

inline operator fun <reified T : RingElement<T>> Array<T>.unaryPlus() = this
inline operator fun <reified T : RingElement<T>> Array<T>.unaryMinus() = Array(size) { i -> -this[i] }


fun <T : RingElement<T>> Array<T>.normSquare() = this * this

infix fun <T : RingElement<T>> Array<T>.dot(other: Array<T>): T = this * other

inline infix fun <reified T : RingElement<T>> Array<T>.cross(other: Array<T>): Array<T> {
    require(size == other.size && size == 3) { "Arrays must be of size 3" }
    return Array(3) {
        when (it) {
            0 -> (get(1) * other[2]) - (get(2) * other[1])
            1 -> (get(2) * other[0]) - (get(0) * other[2])
            else -> (get(0) * other[1]) - (get(1) * other[0])
        }
    }
}

fun < T : RingElement<T>> Array<T>.transform(f: (T) -> T): Array<T> {
    for (i in indices) {
        this[i] = f(this[i])
    }
    return this
}
