package org.alc.math.matrix

import org.alc.math.rational.Rational
import org.alc.util.matrix.Matrix
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

object DoubleMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Double) =
        Matrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, 0.0)
    operator fun invoke(matrix: Matrix<Double>) = Matrix(matrix)

    fun identity(size: Int) = Matrix(size, size) { i, j -> if (i == j) 1.0 else 0.0 }

    class GaussianSolver(private val matrix: Matrix<Double>) {

        private fun computeGreatest(iteration: Int, greatest:DoubleArray): Unit {
            for (row in iteration until matrix.nbRows) {
                greatest[row] = matrix[row,row].absoluteValue
                for (col in row + 1 until matrix.nbRows) {
                    greatest[row] = max(greatest[row], matrix[row,col].absoluteValue)
                }
            }
        }

        private fun gaussianElimination(): PivotResult {
            if (matrix.nbRows == 0 || matrix.nbColumns == 0) return PivotResult.SINGULAR

            val greatest = DoubleArray(matrix.nbRows)

            //Gaussian elimination:
            var result = PivotResult.NO_SWAP
            for (iteration in 0 until matrix.nbRows) {
                computeGreatest(iteration, greatest)
                val ratios = scaleRatios(iteration, greatest)
                val tmp = invert(iteration, ratios)
                if (tmp == PivotResult.SINGULAR)
                    return PivotResult.SINGULAR
                else if (tmp == PivotResult.SWAP)
                    result = result.toggle()
            }
            return result
        }

        fun determinant(): Double {
            var result = when (gaussianElimination()) {
                PivotResult.SINGULAR -> return 0.0
                PivotResult.SWAP -> -1.0
                else -> 1.0
            }
            for (i in 0 until matrix.nbRows) {
                result *= matrix[i, i]
            }
            return result
        }

        fun solve(): DoubleArray {
            invert()
            return DoubleArray(matrix.nbRows) { i -> matrix[i, matrix.nbRows] }
        }

        fun invert() {
            val result = gaussianElimination()
            if (result == PivotResult.SINGULAR) {
                throw ArithmeticException("Matrix is singular")
            }

            backSubstitution()
        }

        private fun backSubstitution() {
            for (row in matrix.nbRows - 1 downTo 0) {
                val pivot = matrix[row, row]
                for (row1 in 0 until row) {
                    val factor = matrix[row1, row] / pivot
                    for (col in row until matrix.nbColumns) {
                        matrix[row1, col] -= factor * matrix[row, col]
                    }
                }
                matrix[row, row] = 1.0
                for (col in matrix.nbRows until matrix.nbColumns) {
                    matrix[row, col] /= pivot
                }
            }
        }


        private fun scaleRatios(iteration: Int, greatest: DoubleArray): DoubleArray {
            val ratios = DoubleArray(matrix.nbRows)
            for (row in iteration until matrix.nbRows) {
                if (greatest[row] > 0.0) {
                    ratios[row] = abs(matrix[row, iteration] / greatest[row])
                }
            }
            return ratios
        }

        enum class PivotResult {
            SWAP,
            NO_SWAP,
            SINGULAR;

            fun toggle() = when (this) {
                SWAP -> NO_SWAP
                NO_SWAP -> SWAP
                else -> SINGULAR

            }
        }

        private fun pivotAndSwap(iteration: Int, ratios: DoubleArray): PivotResult {

            var maxRatio = ratios[iteration]
            var maxIdx = iteration
            for (i in iteration until matrix.nbRows) {
                if (maxRatio < ratios[i]) {
                    maxRatio = ratios[i]
                    maxIdx = i
                }
            }
            var result = PivotResult.NO_SWAP
            if (maxIdx != iteration) {
                matrix.swapRows(iteration, maxIdx)
                result = PivotResult.SWAP
            }
            if (matrix[iteration, iteration].absoluteValue <= 1e-10) {
                result = PivotResult.SINGULAR
            }
            return result
        }


        private fun invert(iteration: Int, ratios: DoubleArray): PivotResult {
            val result = pivotAndSwap(iteration, ratios)
            if (result == PivotResult.SINGULAR) return result
            for (row in iteration + 1 until matrix.nbRows) {
                val value = matrix[row, iteration] / matrix[iteration, iteration]
                if (value != 0.0) {
                    for (col in iteration until matrix.nbColumns) {
                        matrix[row, col] -= value * matrix[iteration, col]
                    }
                }
            }
            return result
        }
    }

}

fun Matrix<Double>.determinant(): Double {
    requireIsSquare()
    val work = Matrix(this)
    val gauss = DoubleMatrix.GaussianSolver(work)
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
    val gauss = DoubleMatrix.GaussianSolver(work)
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
    DoubleMatrix.GaussianSolver(work).invert()
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




