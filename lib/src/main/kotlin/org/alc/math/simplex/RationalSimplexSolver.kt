package org.alc.math.simplex

import org.alc.math.rational.Rational
import org.alc.parser.*
import org.alc.util.matrix.Matrix

object RationalSimplexSolver {

    fun solve(z: String, vararg constraints: String): Pair<Map<String, Rational>, Rational> {
        return solve(
            Parser(z).parseObjective(),
            *constraints.map { Parser(it).parseConstraint()}.toTypedArray()
        )
    }

    fun solve(z: ObjectiveFunction, vararg constraints: ConstraintFunction): Pair<Map<String, Rational>, Rational> {
        require(z.obj == Objective.Max) { "Only Max is supported for objective function" }
        require(z.variables.isNotEmpty()) { "The objective function must have variables" }
        require(constraints.isNotEmpty()) { "At least one constraint is required" }
        require(constraints.all { it.variables.isNotEmpty() }) { "All constraint variables must be non empty" }
        require(constraints.all { it.comp == Comparator.LessThanOrEqual })

        // crate name - idx mapping
        val names = buildSet {
            addAll(z.variables.keys)
            constraints.forEach { constraint -> addAll(constraint.variables.keys) }
        }.toList()

        val zArr = Array(names.size) { i -> z.variables[names[i]] ?: Rational.ZERO }
        val m = Matrix(constraints.size, names.size) { i, j ->
            constraints[i].variables[names[j]] ?: Rational.ZERO
        }
        val c = constraints.map { it.value }.toTypedArray()
        val result = solve(zArr, m, c)

        return Pair(
            buildMap { names.forEachIndexed() { i, v -> set(v, result.first[i]) } },
            result.second
        )
    }

    fun solve(z: Array<Rational>, a: Matrix<Rational>, c: Array<Rational>): Pair<List<Rational>, Rational> {
        require(z.size == a.nbColumns)
        { "objective function must have same number of variables than constraint matrix" }
        require(a.nbRows == c.size)
        { "constraint matrix must have same number of rows as constraint array" }

        val m = initMatrix(z, a, c)
        return solve(m)
    }

    private fun findValues(m: Matrix<Rational>) =
        Array(m.nbColumns - 1) { solveVariable(it, m) }

    private fun solveVariable(col: Int, m: Matrix<Rational>): Rational {
        var rowFound = -1
        for (row in 0 until m.nbRows) {
            if (m[row, col] != Rational.ZERO) {
                if (rowFound >= 0 || m[row, col] != Rational.ONE) return Rational.ZERO
                rowFound = row
            }
        }
        return m[rowFound, m.nbColumns - 1]
    }

    private fun initMatrix(z: Array<Rational>, a: Matrix<Rational>, c: Array<Rational>): Matrix<Rational> {
        val nbSlacks = a.nbRows + 1
        val m = Matrix(a.nbRows + 1, a.nbColumns + nbSlacks + 1, Rational.ZERO)

        for (j in 0 until a.nbColumns) m[0, j] = -z[j]
        m[0, a.nbColumns + nbSlacks - 1] = Rational.ONE

        for (i in 0 until a.nbRows) {
            val i1 = i + 1
            for (j in 0 until a.nbColumns) {
                m[i1, j] = a[i, j]
            }
            for (j in a.nbColumns until a.nbColumns + nbSlacks) {
                m[i1, j] = if (j == a.nbColumns + i) Rational.ONE else Rational.ZERO
            }
            m[i1, a.nbColumns + nbSlacks] = c[i]
        }
        return m
    }

    private fun solve(m: Matrix<Rational>): Pair<List<Rational>, Rational> {
        var iteration = 0
        var minCol = findMinColumn(m)
        while (minCol >= 0 && iteration < 10) {
            val pivotRow = findPivotRow(m, minCol)
            if (pivotRow < 0) return Pair(findValues(m).asList(), Rational.NaN)
            pivot(m, pivotRow, minCol)
            minCol = findMinColumn(m)
            iteration++
        }
        //return m
        return Pair(findValues(m).asList(), m[0, m.nbColumns - 1])

    }

    private fun findMinColumn(m: Matrix<Rational>): Int {
        var minCol = 0
        for (j in 1 until m.nbColumns - 1) {
            if (m[0, j] < m[0, minCol]) minCol = j
        }
        return if (m[0, minCol] < Rational.ZERO) minCol else -1
    }

    private fun findPivotRow(m: Matrix<Rational>, minCol: Int): Int {
        var pivotRow = -1
        var minRatio = Rational.MINUS_ONE
        for (row in 1 until m.nbRows) {
            val den = m[row, minCol]
            if (den == Rational.ZERO) continue
            val ratio = m[row, m.nbColumns - 1] / den
            if ((ratio < minRatio || minRatio == Rational.MINUS_ONE) && ratio >= Rational.ZERO) {
                pivotRow = row
                minRatio = ratio
            }
        }
        return pivotRow
    }

    private fun pivot(m: Matrix<Rational>, pivotRow: Int, minCol: Int) {
        var factor = m[pivotRow, minCol]
        for (j in 0 until m.nbColumns) m[pivotRow, j] /= factor
        for (row in 0 until m.nbRows) {
            if (row == pivotRow) continue
            factor = m[row, minCol]
            for (j in 0 until m.nbColumns) m[row, j] -= m[pivotRow, j] * factor
        }
    }
}
