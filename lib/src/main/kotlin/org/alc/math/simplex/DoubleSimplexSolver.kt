package org.alc.math.simplex

import org.alc.math.matrix.DoubleMatrix
import org.alc.parser.*
import org.alc.util.matrix.Matrix

object DoubleSimplexSolver {

    fun solve(z: String, vararg constraints: String): Pair<Map<String, Double>, Double> {
        return solve(
            DoubleParser(z).parseObjective(),
            *constraints.map { DoubleParser(it).parseConstraint() }.toTypedArray()
        )
    }

    fun solve(z: ObjectiveFunction<Double>, vararg constraints: ConstraintFunction<Double>): Pair<Map<String, Double>, Double> {
        require(z.obj == Objective.Max) { "Only Max is supported for objective function" }
        require(z.variables.isNotEmpty()) { "The objective function must have variables" }
        require(constraints.isNotEmpty()) { "At least one constraint is required" }
        require(constraints.all { it.variables.isNotEmpty() }) { "All constraint variables must be non empty" }
        require(constraints.all { it.comp == CompOp.LessThanOrEqual })

        // crate name - idx mapping
        val names = buildSet {
            addAll(z.variables.keys)
            constraints.forEach { constraint -> addAll(constraint.variables.keys) }
        }.toList()

        val zArr = DoubleArray(names.size) { i -> z.variables[names[i]] ?: 0.0 }
        val m = Matrix(constraints.size, names.size) { i, j ->
            constraints[i].variables[names[j]] ?: 0.0
        }
        val c = constraints.map { it.value }.toDoubleArray()
        val result = solve(zArr, m, c)

        return Pair(
            buildMap { names.forEachIndexed { i, v -> set(v, result.first[i]) } },
            result.second
        )
    }


    fun solve(z: DoubleArray, a: Matrix<Double>, c: DoubleArray): Pair<List<Double>, Double> {
        require(z.size == a.nbColumns)
        { "objective function must have same number of variables than constraint matrix" }
        require(a.nbRows == c.size)
        { "constraint matrix must have same number of rows as constraint array" }

        val m = initMatrix(z, a, c)
        return solve(m)
    }

    private fun findValues(m: Matrix<Double>) =
        DoubleArray(m.nbColumns - 1) { solveVariable(it, m) }

    private fun solveVariable(col: Int, m: Matrix<Double>): Double {
        var rowFound = -1
        for (row in 0 until m.nbRows) {
            if (m[row, col] != 0.0) {
                if (rowFound >= 0 || m[row, col] != 1.0) return 0.0
                rowFound = row
            }
        }
        return m[rowFound, m.nbColumns - 1]
    }

    private fun initMatrix(z: DoubleArray, a: Matrix<Double>, c: DoubleArray): Matrix<Double> {
        val nbSlacks = a.nbRows + 1
        val m = DoubleMatrix(a.nbRows + 1, a.nbColumns + nbSlacks + 1)

        for (j in 0 until a.nbColumns) m[0, j] = -z[j]
        m[0, a.nbColumns + nbSlacks - 1] = 1.0

        for (i in 0 until a.nbRows) {
            val i1 = i + 1
            for (j in 0 until a.nbColumns) {
                m[i1, j] = a[i, j]
            }
            for (j in a.nbColumns until a.nbColumns + nbSlacks) {
                m[i1, j] = if (j == a.nbColumns + i) 1.0 else 0.0
            }
            m[i1, a.nbColumns + nbSlacks] = c[i]
        }
        return m
    }

    private fun solve(m: Matrix<Double>): Pair<List<Double>, Double> {
        var iteration = 0
        var minCol = findMinColumn(m)
        while (minCol >= 0 && iteration < 10) {
            val pivotRow = findPivotRow(m, minCol)
            if (pivotRow < 0) return Pair(findValues(m).asList(), Double.NaN)
            pivot(m, pivotRow, minCol)
            minCol = findMinColumn(m)
            iteration++
        }
        //return m
        return Pair(findValues(m).asList(), m[0, m.nbColumns - 1])

    }

    private fun findMinColumn(m: Matrix<Double>): Int {
        var minCol = 0
        for (j in 1 until m.nbColumns - 1) {
            if (m[0, j] < m[0, minCol]) minCol = j
        }
        return if (m[0, minCol] < 0.0) minCol else -1
    }

    private fun findPivotRow(m: Matrix<Double>, minCol: Int): Int {
        var pivotRow = -1
        var minRatio = Double.POSITIVE_INFINITY
        for (row in 1 until m.nbRows) {
            val den = m[row, minCol]
            if (den == 0.0) continue
            val ratio = m[row, m.nbColumns - 1] / den
            if (ratio < minRatio && ratio >= 0.0) {
                pivotRow = row
                minRatio = ratio
            }
        }
        return pivotRow
    }

    private fun pivot(m: Matrix<Double>, pivotRow: Int, minCol: Int) {
        var factor = m[pivotRow, minCol]
        for (j in 0 until m.nbColumns) m[pivotRow, j] /= factor
        for (row in 0 until m.nbRows) {
            if (row == pivotRow) continue
            factor = m[row, minCol]
            for (j in 0 until m.nbColumns) m[row, j] -= m[pivotRow, j] * factor
        }
    }
}
