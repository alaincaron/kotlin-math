package org.alc.math.matrix

import org.alc.math.ring.Ring
import org.alc.math.ring.RingElement
import org.alc.util.matrix.Matrix

interface RingMatrixFactory<T : RingElement<T>> {
    fun zeroes(nbRows: Int, nbColumns: Int): Matrix<T>
    fun identity(size: Int): Matrix<T>
}

class DefaultRingMatrixFactory<T : RingElement<T>>(private val ring: Ring<T>) : RingMatrixFactory<T> {
    override fun zeroes(nbRows: Int, nbColumns: Int) =
        Matrix(nbRows, nbColumns, ring.zero())

    override fun identity(size: Int) =
        Matrix(size, size) { i, j -> if (i == j) ring.one() else ring.zero() }
}

operator fun <T : RingElement<T>> Matrix<T>.plus(other: Matrix<T>): Matrix<T> {
    require(sameDimensions(other)) { "Matrices must be of same dimension" }
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] + other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.minus(other: Matrix<T>): Matrix<T> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] - other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.times(other: Matrix<T>): Matrix<T> {
    require(compatibleForMultiplication(other))
    return Matrix(nbRows, other.nbColumns) { i, j ->
        var sum = this[i, 0] * other[0,j]
        for (k in 1 until this.nbColumns) {
            sum += this[i, k] * other[k, j]
        }
        sum
    }
}

operator fun <T : RingElement<T>> Matrix<T>.plusAssign(other: Matrix<T>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v + other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.minusAssign(other: Matrix<T>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v - other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.unaryMinus() =
    Matrix(nbRows, nbColumns) { i, j -> -this[i, j] }

operator fun <T : RingElement<T>> Matrix<T>.unaryPlus() = Matrix(this)



