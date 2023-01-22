package org.alc.math.matrix

import org.alc.util.matrix.Matrix

object LongMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Long) =
        Matrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, 0L)
    operator fun invoke(matrix: Matrix<Long>) = Matrix(matrix)

    fun identity(size: Int) = Matrix(size, size) { i, j -> if (i == j) 1L else 0L }
}

operator fun Matrix<Long>.plus(other: Matrix<Long>): Matrix<Long> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] + other[i, j] }
}

operator fun Matrix<Long>.minus(other: Matrix<Long>): Matrix<Long> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] - other[i, j] }
}

operator fun Matrix<Long>.times(other: Matrix<Long>): Matrix<Long> {
    require(compatibleForMultiplication(other))
    return Matrix(nbRows, other.nbColumns) { i, j ->
        var sum = 0L
        for (k in 0 until this.nbColumns) {
            sum += this[i, k] * other[k, j]
        }
        sum
    }
}

operator fun Matrix<Long>.plusAssign(other: Matrix<Long>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v + other[i, j] }
}

operator fun Matrix<Long>.minusAssign(other: Matrix<Long>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v - other[i, j] }
}

operator fun Matrix<Long>.unaryMinus() =
    Matrix(nbRows, nbColumns) { i, j -> -this[i, j] }

operator fun Matrix<Long>.unaryPlus() = Matrix(this)


