package org.alc.math.matrix

import org.alc.util.matrix.Matrix

object DoubleMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Double) =
        Matrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, 0.0)
    operator fun invoke(matrix: Matrix<Double>) = Matrix(matrix)

    fun identity(size: Int) = Matrix(size, size) { i, j -> if (i == j) 1.0 else 0.0 }

}

fun Matrix<Double>.determinant(): Double {
    requireIsSquare()
    val work = Matrix(this)
    val gauss = DoubleGaussianElimination(work)
    return gauss.determinant()
}

fun Matrix<Double>.invert(): Matrix<Double> {
    val work = invertBase()
    return Matrix(nbRows, nbColumns) { i, j -> work[i, j + nbColumns] }
}

fun Matrix<Double>.invertTransform(): Matrix<Double> {
    val work = invertBase()
    return transformIndexed { i, j, _ -> work[i, j + nbColumns] }
}


fun Matrix<Double>.solve(values: DoubleArray): DoubleArray {
    require(nbRows == values.size) { "Matrix and values must have same number of rows" }
    val work = Matrix(
        nbRows,
        nbColumns + 1
    ) { i, j -> if (j == nbColumns) values[i] else this[i, j] }
    val gauss = DoubleGaussianElimination(work)
    return gauss.solve()

}

private fun Matrix<Double>.invertBase(): Matrix<Double> {
    requireIsSquare()
    val boundary = nbColumns - 1
    val work = Matrix(nbRows, 2 * nbColumns) { i, j ->
        when (j) {
            in 0..boundary -> this[i, j]
            i + nbColumns -> 1.0
            else -> 0.0
        }
    }
    DoubleGaussianElimination(work).invert()
    return work
}


operator fun Matrix<Double>.plus(other: Matrix<Double>): Matrix<Double> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] + other[i, j] }
}

operator fun Matrix<Double>.minus(other: Matrix<Double>): Matrix<Double> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] - other[i, j] }
}

operator fun Matrix<Double>.times(other: Matrix<Double>): Matrix<Double> {
    requireCompatibleForMultiplication(other)
    return Matrix(nbRows, other.nbColumns) { i, j ->
        var sum = 0.0
        for (k in 0 until this.nbColumns) {
            sum += this[i, k] * other[k, j]
        }
        sum
    }
}

operator fun Matrix<Double>.plusAssign(other: Matrix<Double>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v + other[i, j] }
}

operator fun Matrix<Double>.minusAssign(other: Matrix<Double>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v - other[i, j] }
}

operator fun Matrix<Double>.unaryMinus() =
    Matrix(nbRows, nbColumns) { i, j -> -this[i, j] }

operator fun Matrix<Double>.unaryPlus() = Matrix(this)




