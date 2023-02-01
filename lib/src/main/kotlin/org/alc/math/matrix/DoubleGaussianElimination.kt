package org.alc.math.matrix

import org.alc.util.matrix.Matrix
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

class DoubleGaussianElimination(private val matrix: Matrix<Double>) {

    private fun computeGreatest(iteration: Int, greatest:DoubleArray) {
        for (row in iteration until matrix.nbRows) {
            greatest[row] = matrix[row,row].absoluteValue
            for (col in row + 1 until matrix.nbRows) {
                greatest[row] = max(greatest[row], matrix[row, col].absoluteValue)
            }
        }
    }

    fun gaussianElimination(): PivotResult {
        if (matrix.nbRows == 0 || matrix.nbColumns == 0) return PivotResult.SINGULAR

        val greatest = DoubleArray(matrix.nbRows)

        //Gaussian elimination:
        var result = PivotResult.NO_SWAP
        for (iteration in 0 until matrix.nbRows) {
            computeGreatest(iteration, greatest)
            val ratios = scaleRatios(iteration, greatest)
            result = result.transition(pivotAndReduce(iteration, ratios))
            if (result == PivotResult.SINGULAR) break
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


    private fun pivotAndReduce(iteration: Int, ratios: DoubleArray): PivotResult {
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