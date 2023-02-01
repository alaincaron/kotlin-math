package org.alc.math.vector

import kotlin.math.sqrt

operator fun LongArray.times(other: LongArray): Long {
    require(size == other.size) { "Arrays must be of the same size" }
    var sum = 0L
    for (i in indices) sum += this[i] * other[i]
    return sum
}

operator fun LongArray.times(other: Long) = LongArray(size) { i -> this[i] * other }
operator fun LongArray.times(other: Int) = LongArray(size) { i -> this[i] * other }
operator fun Long.times(other: LongArray) = other * this
operator fun Int.times(other: LongArray) = other * this

operator fun LongArray.minus(other: LongArray): LongArray {
    require(size == other.size) { "Arrays must be of the same size" }
    return LongArray(size) { i -> this[i] - other[i] }
}

operator fun LongArray.plus(other: LongArray): LongArray {
    require(size == other.size) { "Arrays must be of the same size" }
    return LongArray(size) { i -> this[i] + other[i] }
}

operator fun LongArray.unaryPlus() = this
operator fun LongArray.unaryMinus() = LongArray(size) { i -> -this[i] }

fun LongArray.normSquare() = this * this
fun LongArray.norm() = sqrt(normSquare().toDouble())

infix fun LongArray.dot(other: LongArray): Long = this * other

infix fun LongArray.cross(other: LongArray): LongArray {
    require(size == other.size && size == 3) { "Arrays be must of size 3" }
    return LongArray(3) {
        when (it) {
            0 -> (get(1) * other[2]) - (get(2) * other[1])
            1 -> (get(2) * other[0]) - (get(0) * other[2])
            else -> (get(0) * other[1]) - (get(1) * other[0])
        }
    }
}

fun LongArray.transform(f: (Long) -> Long): LongArray {
    for (i in indices) {
        this[i] = f(this[i])
    }
    return this
}

