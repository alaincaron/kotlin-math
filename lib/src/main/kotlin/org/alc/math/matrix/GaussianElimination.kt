package org.alc.math.matrix

import org.alc.math.ring.DivisionRing
import org.alc.util.matrix.Matrix

class GaussianElimination<T : Comparable<T>>(
    private val ring: DivisionRing<T>, private val matrix: Matrix<T>
) {

    private fun abs(x: T) = if (x < ring.zero()) ring.negate(x) else x
    private fun max(a: T, b: T) = if (a > b) a else b
    private operator fun T.times(b: T) = ring.multiply(this, b)
    private operator fun T.div(b: T) = ring.divide(this, b)
    private operator fun T.minus(b: T) = ring.subtract(this, b)

    private fun computeGreatest(iteration: Int, greatest: MutableList<T>) {
        for (row in iteration until matrix.nbRows) {
            greatest[row] = abs(matrix[row, row])
            for (col in row + 1 until matrix.nbRows) {
                greatest[row] = max(greatest[row], abs(matrix[row, col]))
            }
        }
    }

    private fun gaussianElimination(): PivotResult {
        if (matrix.nbRows == 0 || matrix.nbColumns == 0) return PivotResult.SINGULAR

        //find the largest absolute value for each row to use in scale ratios
        val greatest = MutableList(matrix.nbRows) { ring.zero() }

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

    fun determinant(): T {
        var result = when (gaussianElimination()) {
            PivotResult.SINGULAR -> return ring.zero()
            PivotResult.SWAP -> ring.negate(ring.one())
            else -> ring.one()
        }
        for (i in 0 until matrix.nbRows) {
            result *= matrix[i, i]
        }
        return result
    }

    fun solve(): List<T> {
        invert()
        return List(matrix.nbRows) { i -> matrix[i, matrix.nbRows] }
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
                val factor = ring.divide(matrix[row1, row], pivot)
                for (col in row until matrix.nbColumns) {
                    matrix[row1, col] -= factor * matrix[row, col]
                }
            }
            matrix[row, row] = ring.one()
            for (col in matrix.nbRows until matrix.nbColumns) {
                matrix[row, col] /= pivot
            }
        }
    }


    private fun scaleRatios(iteration: Int, greatest: List<T>): MutableList<T> {
        val ratios = MutableList(matrix.nbRows) { ring.zero() }
        for (row in iteration until matrix.nbRows) {
            if (greatest[row] > ring.zero()) {
                ratios[row] = abs(matrix[row, iteration] / greatest[row])
            }
        }
        return ratios
    }

    private fun pivotAndSwap(iteration: Int, ratios: List<T>): PivotResult {

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
        if (matrix[iteration, iteration] == ring.zero()) {
            result = PivotResult.SINGULAR
        }
        return result
    }


    private fun pivotAndReduce(iteration: Int, ratios: List<T>): PivotResult {
        val result = pivotAndSwap(iteration, ratios)
        if (result == PivotResult.SINGULAR) return result
        for (row in iteration + 1 until matrix.nbRows) {
            val value = matrix[row, iteration] / matrix[iteration, iteration]
            if (value.compareTo(ring.zero()) != 0) {
                for (col in iteration until matrix.nbColumns) {
                    matrix[row, col] -= value * matrix[iteration, col]
                }
            }
        }
        return result
    }
}

