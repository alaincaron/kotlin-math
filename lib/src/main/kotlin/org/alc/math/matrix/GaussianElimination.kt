package org.alc.math.matrix

import org.alc.math.ring.DivisionRingElement
import org.alc.math.ring.Ring
import org.alc.util.matrix.Matrix

object GaussianSolver {
    fun <T> determinant(ring: Ring<T>, m: Matrix<T>): T where T : DivisionRingElement<T>, T : Comparable<T> {
        m.requireIsSquare()
        val work = Matrix(m)
        val gauss = GaussianElimination(ring, work)
        return gauss.determinant()
    }

    fun <T> invert(ring: Ring<T>, m: Matrix<T>): Matrix<T> where T : DivisionRingElement<T>, T : Comparable<T> {
        val work = invertBase(ring, m)
        return Matrix(m.nbRows, m.nbColumns) { i, j -> work[i, j + m.nbColumns] }
    }

    fun <T> invertTransform(
        ring: Ring<T>,
        m: Matrix<T>
    ): Matrix<T> where T : DivisionRingElement<T>, T : Comparable<T> {
        val work = invertBase(ring, m)
        return m.transformIndexed { i, j, _ -> work[i, j + m.nbColumns] }
    }

    fun <T> solve(
        ring: Ring<T>,
        m: Matrix<T>,
        values: List<T>
    ): List<T> where T : DivisionRingElement<T>, T : Comparable<T> {
        require(m.nbRows == values.size)
        { "Matrix and values must have same number of rows" }
        val work = Matrix(
            m.nbRows,
            m.nbColumns + 1
        ) { i, j -> if (j == m.nbColumns) values[i] else m[i, j] }
        val gauss = GaussianElimination(ring, work)
        return gauss.solve()
    }

    fun <T> solve(
        ring: Ring<T>,
        m: Matrix<T>,
        values: Array<T>
    ) where T : DivisionRingElement<T>, T : Comparable<T> = solve(ring, m, values.asList())


    private fun <T> invertBase(
        ring: Ring<T>,
        m: Matrix<T>
    ): Matrix<T> where T : DivisionRingElement<T>, T : Comparable<T> {
        m.requireIsSquare()
        val boundary = m.nbColumns - 1
        val work = Matrix(m.nbRows, 2 * m.nbColumns) { i, j ->
            when (j) {
                in 0..boundary -> m[i, j]
                i + m.nbColumns -> ring.one()
                else -> ring.zero()
            }
        }
        GaussianElimination(ring, work).invert()
        return work
    }
}


class GaussianElimination<T>(
    private val ring: Ring<T>, private val matrix: Matrix<T>
) where T : DivisionRingElement<T>, T : Comparable<T> {

    private fun abs(x: T) = if (x < ring.zero()) -x else x
    private fun max(a: T, b: T) = if (a > b) a else b

    private fun computeGreatest(iteration: Int, greatest: MutableList<T>) {
        for (row in iteration until matrix.nbRows) {
            greatest[row] = abs(matrix[row, row])
            for (col in row + 1 until matrix.nbRows) {
                greatest[row] = max(greatest[row], abs(matrix[row, col]))
            }
        }
    }

    fun gaussianElimination(): PivotResult {
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
            PivotResult.SWAP -> ring.zero() - ring.one()
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
                val factor = matrix[row1, row] / pivot
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

