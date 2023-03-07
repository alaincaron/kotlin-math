package org.alc.math.vector

import kotlin.math.sqrt

fun requireSameSize(a: DoubleArray, b: DoubleArray) {
    require(a.size == b.size) { "Arrays must be of same size" }
}

operator fun DoubleArray.plus(other: DoubleArray): DoubleArray {
    requireSameSize(this, other)
    return DoubleArray(size) { i -> this[i] + other[i] }
}

operator fun DoubleArray.minus(other: DoubleArray): DoubleArray {
    requireSameSize(this, other)
    return DoubleArray(size) { i -> this[i] - other[i] }
}

operator fun DoubleArray.times(other: DoubleArray): Double {
    requireSameSize(this, other)
    var sum = 0.0
    for (i in indices) sum += this[i] * other[i]
    return sum
}

operator fun Double.times(other: DoubleArray) = other * this
operator fun DoubleArray.times(other: Double) = DoubleArray(size) { i -> this[i] * other }
operator fun DoubleArray.times(other: Number) = this * other.toDouble()
operator fun DoubleArray.div(other: Double) = DoubleArray(size) { i -> this[i] / other }
operator fun DoubleArray.div(other: Number) = this / other.toDouble()

operator fun DoubleArray.unaryPlus() = this
operator fun DoubleArray.unaryMinus() = DoubleArray(size) { i -> -this[i] }


fun DoubleArray.normSquare() = this * this
fun DoubleArray.norm() = sqrt(normSquare())

infix fun DoubleArray.dot(other: DoubleArray): Double = this * other

infix fun DoubleArray.cross(other: DoubleArray): DoubleArray {
    require(size == other.size && size == 3) { "Arrays must be of size 3" }
    return DoubleArray(3) {
        when (it) {
            0 -> (get(1) * other[2]) - (get(2) * other[1])
            1 -> (get(2) * other[0]) - (get(0) * other[2])
            else -> (get(0) * other[1]) - (get(1) * other[0])
        }
    }
}

fun DoubleArray.transform(f: (Double) -> Double): DoubleArray {
    forEachIndexed { index, item -> this[index] = f(item)}
    return this
}
fun DoubleArray.transformIndexed(f: (Int, Double) -> Double): DoubleArray {
    forEachIndexed { index, item -> this[index] = f(index, item)}
    return this
}

infix fun DoubleArray.project(base: DoubleArray) =
    ((this * base) / base.normSquare()) * base

