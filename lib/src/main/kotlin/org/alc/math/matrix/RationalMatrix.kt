package org.alc.math.matrix

import org.alc.math.rational.Rational
import org.alc.util.matrix.Matrix

object RationalMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Rational) =
        Matrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, Rational.ZERO)
    operator fun invoke(matrix: Matrix<Rational>) = Matrix(matrix)

    fun identity(size: Int) = Matrix(size, size) { i, j -> if (i == j) Rational.ONE else Rational.ZERO }

    class GaussianSolver(private val matrix: Matrix<Rational>) {

        private fun computeGreatest(iteration: Int, greatest:Array<Rational>) {
            for (row in iteration until matrix.nbRows) {
                greatest[row] = matrix[row,row].abs()
                for (col in row + 1 until matrix.nbRows) {
                    greatest[row] = greatest[row].max(matrix[row,col].abs())
                }
            }
        }

        private fun gaussianElimination(): PivotResult {
            if (matrix.nbRows == 0 || matrix.nbColumns == 0) return PivotResult.SINGULAR

            //find the largest absolute value for each row to use in scale ratios
            val greatest = Array(matrix.nbRows) { Rational.ZERO}

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

        fun determinant(): Rational {
            var result = when (gaussianElimination()) {
                PivotResult.SINGULAR -> return Rational.ZERO
                PivotResult.SWAP -> Rational.MINUS_ONE
                else -> Rational.ONE
            }
            for (i in 0 until matrix.nbRows) {
                result *= matrix[i, i]
            }
            return result
        }

        fun solve(): Array<Rational> {
            invert()
            return Array(matrix.nbRows) { i -> matrix[i, matrix.nbRows] }
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
                matrix[row, row] = Rational.ONE
                for (col in matrix.nbRows until matrix.nbColumns) {
                    matrix[row, col] /= pivot
                }
            }
        }


        private fun scaleRatios(iteration: Int, greatest: Array<Rational>): Array<Rational> {
            val ratios = Array(matrix.nbRows) { Rational.ZERO }
            for (row in iteration until matrix.nbRows) {
                if (greatest[row] > Rational.ZERO) {
                    ratios[row] = (matrix[row, iteration] / greatest[row]).abs()
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

        private fun pivotAndSwap(iteration: Int, ratios: Array<Rational>): PivotResult {

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
            if (matrix[iteration, iteration] == Rational.ZERO) {
                result = PivotResult.SINGULAR
            }
            return result
        }


        private fun invert(iteration: Int, ratios: Array<Rational>): PivotResult {
            val result = pivotAndSwap(iteration, ratios)
            if (result == PivotResult.SINGULAR) return result
            for (row in iteration + 1 until matrix.nbRows) {
                val value = matrix[row, iteration] / matrix[iteration, iteration]
                if (value != Rational.ZERO) {
                    for (col in iteration until matrix.nbColumns) {
                        matrix[row, col] -= value * matrix[iteration, col]
                    }
                }
            }
            return result
        }
    }

}

fun Matrix<Rational>.determinant(): Rational {
    requireIsSquare()
    val work = Matrix(this)
    val gauss = RationalMatrix.GaussianSolver(work)
    return gauss.determinant()
}

fun Matrix<Rational>.invert(): Matrix<Rational> {
    val work = invertBase()
    return Matrix(nbRows, nbColumns) { i, j -> work[i, j + nbColumns] }
}

fun Matrix<Rational>.invertTransform(): Matrix<Rational> {
    val work = invertBase()
    return transformIndexed { i, j, _ -> work[i, j + nbColumns] }
}

fun Matrix<Rational>.solve(values: Array<Rational>): Array<Rational> {
    require(nbRows == values.size)
    { "Matrix and values must have same number of rows" }
    val work = Matrix(
        nbRows,
        nbColumns + 1
    ) { i, j -> if (j == nbColumns) values[i] else this[i, j] }
    val gauss = RationalMatrix.GaussianSolver(work)
    return gauss.solve()
}

private fun Matrix<Rational>.invertBase(): Matrix<Rational> {
    requireIsSquare()
    val boundary = nbColumns - 1
    val work = Matrix(nbRows, 2 * nbColumns) { i, j ->
        when (j) {
            in 0..boundary -> this[i, j]
            i + nbColumns -> Rational.ONE
            else -> Rational.ZERO
        }
    }
    RationalMatrix.GaussianSolver(work).invert()
    return work
}


operator fun Matrix<Rational>.plus(other: Matrix<Rational>): Matrix<Rational> {
    require(sameDimensions(other)) { "Matrices must be of same dimension" }
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] + other[i, j] }
}

operator fun Matrix<Rational>.minus(other: Matrix<Rational>): Matrix<Rational> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] - other[i, j] }
}

operator fun Matrix<Rational>.times(other: Matrix<Rational>): Matrix<Rational> {
    require(compatibleForMultiplication(other))
    return Matrix(nbRows, other.nbColumns) { i, j ->
        var sum = Rational.ZERO
        for (k in 0 until this.nbColumns) {
            sum += this[i, k] * other[k, j]
        }
        sum
    }
}

operator fun Matrix<Rational>.plusAssign(other: Matrix<Rational>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v + other[i, j] }
}

operator fun Matrix<Rational>.minusAssign(other: Matrix<Rational>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v - other[i, j] }
}

operator fun Matrix<Rational>.unaryMinus() =
    Matrix(nbRows, nbColumns) { i, j -> -this[i, j] }

operator fun Matrix<Rational>.unaryPlus() = Matrix(this)




