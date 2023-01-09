package org.alc.math.matrix

import org.alc.util.matrix.Matrix
import org.alc.util.matrix.MutableMatrix
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.max

class DoubleMatrix : Matrix<Double> {
    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> Double) : super(nbRows, nbColumns, f)
    constructor(nbRows: Int, nbColumns: Int, value: Double) : this(nbRows, nbColumns, { _, _ -> value })
    constructor(nbRows: Int, nbColumns: Int) : this(nbRows, nbColumns, 0.0)
    constructor(values: MutableDoubleMatrix) : super(values)

    companion object {
        fun identity(size: Int) = DoubleMatrix(size, size) { i, j -> if (i == j) 1.0 else 0.0 }
    }

    override fun transpose(): DoubleMatrix {
        return DoubleMatrix(nbColumns, nbRows, transposeFunction(this))
    }
}

class MutableDoubleMatrix : MutableMatrix<Double> {
    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> Double) : super(nbRows, nbColumns, f)
    constructor(nbRows: Int, nbColumns: Int, value: Double) : this(nbRows, nbColumns, { _, _ -> value })
    constructor(nbRows: Int, nbColumns: Int) : this(nbRows, nbColumns, 0.0)
    constructor(values: DoubleMatrix) : super(values)
    constructor(values: MutableDoubleMatrix) : super(values)

    companion object {
        fun identity(size: Int) = MutableDoubleMatrix(size, size) { i, j -> if (i == j) 1.0 else 0.0 }
    }

    override fun transpose(): MutableDoubleMatrix {
        return MutableDoubleMatrix(nbColumns, nbRows, transposeFunction(this))
    }

    private fun gaussianElimination(): PivotResult {
        if (nbRows == 0 || nbColumns == 0) return PivotResult.SINGULAR

        //find the largest absolute value for each row to use in scale ratios
        val greatest = DoubleArray(nbRows) { i ->
            rowReduce(i, 0.0) { acc, x -> max(acc, abs(x)) }
        }

        //Gaussian elimination:
        var result = PivotResult.NO_SWAP
        for (i in 0 until nbRows) {
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
        for (i in 0 until nbRows) {
            result *= this[i, i]
        }
        return result
    }

    fun solve(): DoubleArray {
        invert()
        return DoubleArray(nbRows) { i -> this[i,nbRows]}
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
        for (row in nbRows -1  downTo 0) {
            val pivot = this[row,row]
            for (row1 in 0 until row) {
                val factor =  this[row1,row] / pivot
                for (col in row until nbColumns) {
                    this[row1, col] -= factor * this[row,col]
                }
            }
            this[row,row] = 1.0
            for (col in nbRows until nbColumns) {
                this[row,col] /= pivot
            }
        }
    }


    private fun scaleRatios(iteration: Int, greatest: DoubleArray): DoubleArray {
        val ratios = DoubleArray(nbRows)
        for (row in iteration until nbRows - iteration) {
            ratios[row] = abs(this[row, iteration] / greatest[row])
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
        for (i in iteration until nbRows) {
            if (maxRatio < ratios[i]) {
                maxRatio = ratios[i]
                maxIdx = i
            }
        }
        var result = PivotResult.NO_SWAP
        if (maxIdx != iteration) {
            swapRows(iteration, maxIdx)
            result = PivotResult.SWAP
        }
        if (this[iteration, iteration].absoluteValue <= 1e-10) {
            result = PivotResult.SINGULAR
        }
        return result
    }


    //gaussian elimination and display intermediate results
    private fun gaussianElimination(iteration: Int, ratios: DoubleArray): PivotResult {
        val result = pivotAndSwap(iteration, ratios)
        if (result == PivotResult.SINGULAR) return result
        for (row in iteration + 1 until nbRows) {
            val value = this[row, iteration] / this[iteration, iteration]
            if (value != 0.0) {
                for (col in iteration until nbColumns) {
                    this[row, col] -= value * this[iteration, col]
                }
            }
        }
        return result
    }

}


