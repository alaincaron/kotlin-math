package org.alc.math.vector

import kotlin.math.sqrt


fun requireSameSize(a: IntArray, b: IntArray) {
    require(a.size == b.size) { "Arrays must be of same size" }
}

operator fun IntArray.plus(other: IntArray): IntArray {
    return IntArray(size) { i -> this[i] + other[i] }
}

operator fun IntArray.minus(other: IntArray): IntArray {
    requireSameSize(this, other)
    return IntArray(size) { i -> this[i] - other[i] }
}

operator fun IntArray.times(other: IntArray): Int {
    requireSameSize(this, other)
    var sum = 0
    for (i in indices) sum += this[i] * other[i]
    return sum
}

operator fun IntArray.times(other: Int) = IntArray(size) { i -> this[i] * other}
operator fun Int.times(other: IntArray) = other * this

operator fun IntArray.unaryPlus() = this
operator fun IntArray.unaryMinus() = IntArray(size) { i -> -this[i] }

fun IntArray.normSquare() = this * this
fun IntArray.norm() = sqrt(normSquare().toDouble())

infix fun IntArray.dot(other: IntArray): Int = this * other

infix fun IntArray.cross(other: IntArray): IntArray {
    require(size == other.size && size == 3) { "Vectors be must of size 3" }
    return IntArray(3) {
        when (it) {
            0 -> (get(1) * other[2]) - (get(2) * other[1])
            1 -> (get(2) * other[0]) - (get(0) * other[2])
            else -> (get(0) * other[1]) - (get(1) * other[0])
        }
    }
}

fun IntArray.transform(f: (Int) -> Int): IntArray {
    for (i in indices) {
        this[i] = f(this[i])
    }
    return this
}



