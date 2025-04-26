package org.alc.math.vector

import org.alc.math.ring.DoubleRing
import org.alc.util.matrix.Matrix
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

operator fun DoubleArray.timesAssign(other: Double) {
    transform { it * other }
}

operator fun DoubleArray.timesAssign(other: Number) {
    timesAssign(other.toDouble())
}

operator fun DoubleArray.div(other: Double) = DoubleArray(size) { i -> this[i] / other }
operator fun DoubleArray.div(other: Number) = this / other.toDouble()

operator fun DoubleArray.divAssign(other: Double) {
    transform { it / other }
}

operator fun DoubleArray.divAssign(other: Number) {
    divAssign(other.toDouble())
}

operator fun DoubleArray.unaryPlus() = clone()
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
    forEachIndexed { index, item -> this[index] = f(item) }
    return this
}

fun DoubleArray.transformIndexed(f: (Int, Double) -> Double): DoubleArray {
    forEachIndexed { index, item -> this[index] = f(index, item) }
    return this
}

infix fun DoubleArray.project(base: DoubleArray) =
    ((this * base) / base.normSquare()) * base

private object DoubleArrayFactory : DivisionRingVectorFactory<Double>(DoubleRing) {
    override fun create(size: Int, f: (Int) -> Double) = Array(size, f)
}

operator fun Array<Double>.times(other: Array<Double>) = DoubleArrayFactory.multiply(this, other)
operator fun Array<Double>.times(other: Double) = DoubleArrayFactory.multiply(this, other)
operator fun Array<Double>.times(other: Number) = this * other.toDouble()
operator fun Double.times(other: Array<Double>) = DoubleArrayFactory.multiply(this, other)
operator fun Number.times(other: Array<Double>) = toDouble() * other
operator fun Array<Double>.times(other: Matrix<Double>) = DoubleArrayFactory.matrixMultiply(this, other)
operator fun Matrix<Double>.times(other: Array<Double>) = DoubleArrayFactory.matrixMultiply(this, other)
operator fun Array<Double>.plus(other: Array<Double>) = DoubleArrayFactory.add(this, other)
operator fun Array<Double>.minus(other: Array<Double>) = DoubleArrayFactory.subtract(this, other)
operator fun Array<Double>.unaryMinus() = DoubleArrayFactory.unaryMinus(this)
operator fun Array<Double>.unaryPlus() = DoubleArrayFactory.unaryPlus(this)
fun Array<Double>.normSquare() = DoubleArrayFactory.normSquare(this)
fun Array<Double>.norm() = sqrt(normSquare())
infix fun Array<Double>.dot(other: Array<Double>) = DoubleArrayFactory.dot(this, other)
infix fun Array<Double>.cross(other: Array<Double>) = DoubleArrayFactory.cross(this, other)
infix fun Array<Double>.project(other: Array<Double>) = DoubleArrayFactory.projection(this, other)
operator fun Array<Double>.div(other: Double) = DoubleArrayFactory.divide(this, other)
