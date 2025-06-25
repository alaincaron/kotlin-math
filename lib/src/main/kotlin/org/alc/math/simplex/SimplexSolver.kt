package org.alc.math.simplex

import org.alc.math.rational.Rational
import org.alc.math.rational.RationalRing
import org.alc.math.ring.OrderedDivisionRing
import org.alc.parser.*
import org.alc.util.matrix.Matrix

open class SimplexSolver<T : Number, R : OrderedDivisionRing<T>>(
    private val factory: (input: String) -> Tokenizer<T>,
    private val ring: R,
    private val clazz: Class<T>
) {

    fun solve(z: String, vararg constraints: String): Pair<Map<String, T>, T> {
        return solve(
            Parser(factory(z), ring).parseObjective(),
            *constraints.map { Parser(factory(it), ring).parseConstraint() }.toTypedArray()
        )
    }

    private fun createArray(size: Int, initializer: (Int) -> T): Array<T> {
        @Suppress("UNCHECKED_CAST")
        val array = java.lang.reflect.Array.newInstance(clazz, size) as Array<T>
        for (i in 0 until size) array[i] = initializer(i)
        return array
    }

    private fun colToArray(collection: Collection<T>): Array<T> {
        @Suppress("UNCHECKED_CAST")
        val array = java.lang.reflect.Array.newInstance(clazz, collection.size) as Array<T>
        collection.forEachIndexed { i, item ->
            array[i] = item
        }
        return array
    }

    fun solve(z: ObjectiveFunction<T>, vararg constraints: ConstraintFunction<T>): Pair<Map<String, T>, T> {
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

        val zArr = createArray(names.size) { i -> z.variables[names[i]] ?: ring.zero() }
        val m = Matrix(constraints.size, names.size) { i, j ->
            constraints[i].variables[names[j]] ?: ring.zero()
        }
        val c = colToArray(constraints.map { it.value })
        val result = solve(zArr, m, c)

        return Pair(
            buildMap { names.forEachIndexed { i, v -> set(v, result.first[i]) } },
            result.second
        )
    }

    fun solve(z: Array<T>, a: Matrix<T>, c: Array<T>): Pair<List<T>, T> {
        require(z.size == a.nbColumns)
        { "objective function must have same number of variables than constraint matrix" }
        require(a.nbRows == c.size)
        { "constraint matrix must have same number of rows as constraint array" }

        val m = initMatrix(z, a, c)
        return solve(m)
    }

    private fun findValues(m: Matrix<T>) =
        createArray(m.nbColumns - 1) { solveVariable(it, m) }

    private fun solveVariable(col: Int, m: Matrix<T>): T {
        var rowFound = -1
        for (row in 0 until m.nbRows) {
            if (m[row, col] != ring.zero()) {
                if (rowFound >= 0 || m[row, col] != ring.one()) return ring.zero()
                rowFound = row
            }
        }
        return m[rowFound, m.nbColumns - 1]
    }

    private fun initMatrix(z: Array<T>, a: Matrix<T>, c: Array<T>): Matrix<T> {
        val nbSlacks = a.nbRows + 1
        val m = Matrix(a.nbRows + 1, a.nbColumns + nbSlacks + 1, ring.zero())

        for (j in 0 until a.nbColumns) m[0, j] = ring.negate(z[j])
        m[0, a.nbColumns + nbSlacks - 1] = ring.one()

        for (i in 0 until a.nbRows) {
            val i1 = i + 1
            for (j in 0 until a.nbColumns) {
                m[i1, j] = a[i, j]
            }
            for (j in a.nbColumns until a.nbColumns + nbSlacks) {
                m[i1, j] = if (j == a.nbColumns + i) ring.one() else ring.zero()
            }
            m[i1, a.nbColumns + nbSlacks] = c[i]
        }
        return m
    }

    private fun solve(m: Matrix<T>): Pair<List<T>, T> {
        var iteration = 0
        var minCol = findMinColumn(m)
        while (minCol >= 0 && iteration < 10) {
            val pivotRow = findPivotRow(m, minCol)
            if (pivotRow < 0) return Pair(findValues(m).asList(), ring.nan())
            pivot(m, pivotRow, minCol)
            minCol = findMinColumn(m)
            iteration++
        }
        //return m
        return Pair(findValues(m).asList(), m[0, m.nbColumns - 1])

    }

    private fun findMinColumn(m: Matrix<T>): Int {
        var minCol = 0
        for (j in 1 until m.nbColumns - 1) {
            if (ring.compare(m[0, j], m[0, minCol]) < 0) minCol = j
        }
        return if (ring.compare(m[0, minCol], ring.zero()) < 0) minCol else -1
    }

    private fun findPivotRow(m: Matrix<T>, minCol: Int): Int {
        var pivotRow = -1
        var minRatio = ring.negate(ring.one())
        for (row in 1 until m.nbRows) {
            val den = m[row, minCol]
            if (den == ring.zero()) continue
            val ratio = ring.divide(m[row, m.nbColumns - 1], den)
            if ((ring.compare(ratio, minRatio) < 0 || minRatio == ring.negate(ring.one())) && ring.compare(
                    ratio,
                    ring.zero()
                ) >= 0
            ) {
                pivotRow = row
                minRatio = ratio
            }
        }
        return pivotRow
    }

    private fun pivot(m: Matrix<T>, pivotRow: Int, minCol: Int) {
        var factor = m[pivotRow, minCol]
        for (j in 0 until m.nbColumns) m[pivotRow, j] = ring.divide(m[pivotRow, j], factor)
        for (row in 0 until m.nbRows) {
            if (row == pivotRow) continue
            factor = m[row, minCol]
            for (j in 0 until m.nbColumns) m[row, j] = ring.subtract(m[row, j], ring.multiply(m[pivotRow, j], factor))
        }
    }
}

object RationalSimplexSolver : SimplexSolver<Rational, OrderedDivisionRing<Rational>>(::RationalTokenizer, RationalRing, Rational::class.java)




