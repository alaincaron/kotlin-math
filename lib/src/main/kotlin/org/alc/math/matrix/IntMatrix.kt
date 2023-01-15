package org.alc.math.matrix

import org.alc.util.matrix.Matrix

object IntMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Int) =
        Matrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, 0)
    operator fun invoke(matrix: Matrix<Int>) = Matrix(matrix)

    fun identity(size: Int) = Matrix(size, size) { i, j -> if (i == j) 1 else 0 }
}

operator fun Matrix<Int>.plus(other: Matrix<Int>): Matrix<Int> {
    require(sameDimensions(other)) { "Matrices must be of same dimension" }
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] + other[i, j] }
}

operator fun Matrix<Int>.minus(other: Matrix<Int>): Matrix<Int> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] - other[i, j] }
}

operator fun Matrix<Int>.times(other: Matrix<Int>): Matrix<Int> {
    require(compatibleForMultiplication(other))
    return Matrix(nbRows, other.nbColumns) { i, j ->
        var sum = 0
        for (k in 0 until this.nbColumns) {
            sum += this[i, k] * other[k, j]
        }
        sum
    }
}

operator fun Matrix<Int>.plusAssign(other: Matrix<Int>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v + other[i, j] }
}

operator fun Matrix<Int>.minusAssign(other: Matrix<Int>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v - other[i, j] }
}

operator fun Matrix<Int>.unaryMinus() =
    Matrix(nbRows, nbColumns) { i, j -> -this[i, j] }

operator fun Matrix<Int>.unaryPlus() = Matrix(this)


