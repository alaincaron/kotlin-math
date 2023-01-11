package org.alc.math.matrix

import org.alc.util.matrix.AbstractMatrix
import org.alc.util.matrix.Matrix
import org.alc.util.matrix.MutableMatrix
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

object DoubleMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Double) =
        Matrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, 0.0)
    operator fun invoke(matrix: AbstractMatrix<Double>) = Matrix(matrix)

    fun identity(size: Int) = Matrix(size, size) { i, j -> if (i == j) 1.0 else 0.0 }
}

object MutableDoubleMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Double) =
        MutableMatrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, 0.0)
    operator fun invoke(matrix: AbstractMatrix<Double>) = MutableMatrix(matrix)

    fun identity(size: Int) = MutableMatrix(size, size) { i, j -> if (i == j) 1.0 else 0.0 }
}

fun MutableMatrix<Double>.determinant() = GaussianResolver.determinant(this)
fun Matrix<Double>.determinant() = GaussianResolver.determinant(this)

fun MutableMatrix<Double>.invert() = GaussianResolver.invert(this)
fun Matrix<Double>.invert() = GaussianResolver.invert(this)
fun Matrix<Double>.solve(values: DoubleArray) = GaussianResolver.solve(this, values)
fun MutableMatrix<Double>.solve(values: DoubleArray) = GaussianResolver.solve(this, values)

private object GaussianResolver {
    fun solve(
        matrix: AbstractMatrix<Double>,
        values: DoubleArray
    ): DoubleArray {
        require(matrix.nbRows == values.size)
        { "Matrix and values must have same number of rows" }
        val work = MutableMatrix(
            matrix.nbRows,
            matrix.nbColumns + 1
        ) { i, j -> if (j == matrix.nbColumns) values[i] else matrix[i, j] }
        val gauss = GaussianElimination(work)
        return gauss.solve()
    }

    fun determinant(matrix: AbstractMatrix<Double>): Double {
        require(matrix.nbRows == matrix.nbColumns && matrix.nbRows > 0)
        { "Matrix is not square with positive number of rows." }
        val work = MutableMatrix(matrix)
        val gauss = GaussianElimination(work)
        return gauss.determinant()
    }

    fun invert(matrix: MutableMatrix<Double>): MutableMatrix<Double> {
        val work = invertBase(matrix)
        return matrix.transformIndexed { i, j, _ -> work[i, j + matrix.nbColumns] }
    }

    fun invert(matrix: Matrix<Double>): Matrix<Double> {
        val work = invertBase(matrix)
        return Matrix(matrix.nbRows, matrix.nbColumns) { i, j -> work[i, j + matrix.nbColumns] }
    }


    private fun invertBase(matrix: AbstractMatrix<Double>): MutableMatrix<Double> {
        require(matrix.nbRows == matrix.nbColumns && matrix.nbRows > 0)
        { "Matrix is not square with positive number of rows." }
        val boundary = matrix.nbColumns - 1
        val work = MutableMatrix(matrix.nbRows, 2 * matrix.nbColumns) { i, j ->
            when (j) {
                in 0..boundary -> matrix[i, j]
                i + matrix.nbColumns -> 1.0
                else -> 0.0
            }
        }
        GaussianElimination(work).invert()
        return work
    }
}


class GaussianElimination(private val matrix: MutableMatrix<Double>) {

    private fun gaussianElimination(): PivotResult {
        if (matrix.nbRows == 0 || matrix.nbColumns == 0) return PivotResult.SINGULAR

        //find the largest absolute value for each row to use in scale ratios
        val greatest = DoubleArray(matrix.nbRows) { i ->
            matrix.rowReduce(i, 0.0) { acc, x -> max(acc, abs(x)) }
        }

        //Gaussian elimination:
        var result = PivotResult.NO_SWAP
        for (i in 0 until matrix.nbRows) {
            val ratios = scaleRatios(i, greatest)
            val tmp = gaussianElimination(i, ratios)
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
        println("before backSubstitution:\n$this")
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
        for (row in iteration until matrix.nbRows - iteration) {
            ratios[row] = abs(matrix[row, iteration] / greatest[row])
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


    private fun gaussianElimination(iteration: Int, ratios: DoubleArray): PivotResult {
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


