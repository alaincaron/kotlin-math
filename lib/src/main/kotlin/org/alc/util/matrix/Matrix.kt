package org.alc.util.matrix

import java.util.*

class Matrix<T> {
    private val data: Array<Array<Any?>>
    val nbRows: Int
    val nbColumns: Int

    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) {
        require(nbRows > 0 && nbColumns > 0) { "rows and columns must be greater than 0" }
        this.nbRows = nbRows
        this.nbColumns = nbColumns
        data = Array(nbRows) { i -> Array(nbColumns) { j -> f(i, j) } }
    }

    constructor(values: Matrix<T>) : this(values.nbRows, values.nbColumns, { i, j -> values[i, j] })
    constructor(nbRows: Int, nbColumns: Int, value: T) : this(nbRows, nbColumns, { _, _ -> value })

    operator fun get(i: Int, j: Int): T {
        val x = data[i]
        @Suppress("UNCHECKED_CAST")
        return x[j] as T
    }

    operator fun set(i: Int, j: Int, value: T) {
        data[i][j] = value
    }

    fun isSquare() = nbRows == nbColumns
    fun sameDimensions(other: Matrix<*>) = nbRows == other.nbRows && nbColumns == other.nbColumns
    fun compatibleForMultiplication(other: Matrix<*>) = nbColumns == other.nbRows

    fun requireIsSquare() = require(isSquare()) { "Matrix must be square" }
    fun requireSameDimensions(other: Matrix<*>) =
        require(sameDimensions(other)) { "Matrices must be of same dimensions" }

    @Suppress("UNCHECKED_CAST")
    fun <U> forEach(f: (T) -> U): Unit = data.forEach { row -> row.forEach { f(it as T) } }

    fun <U> forEachIndexed(f: (Int, Int, T) -> U): Unit =
        data.forEachIndexed { i, row ->
            row.forEachIndexed { j, item ->
                @Suppress("UNCHECKED_CAST")
                f(i, j, item as T)
            }
        }

    fun <U> rowForEach(row: Int, f: (T) -> U) {
        for (col in 0 until nbColumns) f(get(row, col))
    }

    fun <U> columnForEach(col: Int, f: (T) -> U) {
        for (row in 0 until nbRows) f(get(row, col))
    }

    @Suppress("UNCHECKED_CAST")
    fun <U> rowForEachIndexed(row: Int, f: (Int, Int, T) -> U) =
        data[row].forEachIndexed { j, data -> f(row, j, data as T) }

    fun <U> columnForEachIndexed(col: Int, f: (Int, Int, T) -> U) {
        for (i in 0 until nbRows) {
            f(i, col, get(i, col))
        }
    }

    fun <U> reduce(initial: U, f: (U, T) -> U): U {
        var acc = initial
        forEach { acc = f(acc, it) }
        return acc
    }

    fun <U> reduceIndexed(initial: U, f: (U, T, Int, Int) -> U): U {
        var acc = initial
        forEachIndexed { row, col, v -> acc = f(acc, v, row, col) }
        return acc
    }


    fun <U> rowReduce(row: Int, initial: U, f: (U, T) -> U): U {
        var acc = initial
        for (column in 0 until nbColumns) {
            acc = f(acc, get(row, column))
        }
        return acc
    }

    fun <U> rowReduceIndexed(row: Int, initial: U, f: (U, T, Int, Int) -> U): U {
        var acc = initial
        for (column in 0 until nbColumns) {
            acc = f(acc, get(row, column), row, column)
        }
        return acc
    }

    fun <U> columnReduce(column: Int, initial: U, f: (U, T) -> U): U {
        var acc = initial
        for (row in 0 until nbRows) {
            acc = f(acc, get(row, column))
        }
        return acc
    }

    fun <U> columnReduceIndexed(column: Int, initial: U, f: (U, T, Int, Int) -> U): U {
        var acc = initial
        for (row in 0 until nbRows) {
            acc = f(acc, get(row, column), row, column)
        }
        return acc
    }

    fun swapRows(a: Int, b: Int): Matrix<T> {
        if (a != b) {
            val tmp = data[a]
            data[a] = data[b]
            data[b] = tmp
        }
        return this
    }

    fun swapColumns(a: Int, b: Int): Matrix<T> {
        if (a != b) {
            for (row in 0 until nbRows) {
                val tmp = this[row, a]
                this[row, a] = this[row, b]
                this[row, b] = tmp
            }
        }
        return this
    }

    fun transpose() = Matrix(nbColumns, nbRows) { i: Int, j: Int -> this[j, i] }

    fun <U : T> addRow(row: Int, f: (Int) -> U): Matrix<T> {
        require(row in 0..nbRows) { "row must be within bounds" }
        return Matrix(nbRows + 1, nbColumns) { i, j ->
            when {
                i < row -> this[i, j]
                i == row -> f(j)
                else -> this[i - 1, j]
            }
        }
    }

    fun deleteRow(row: Int): Matrix<T> {
        require(row in 0 until nbRows) { "row must be within bounds" }
        return Matrix(nbRows - 1, nbColumns) { i, j ->
            when {
                i < row -> this[i, j]
                else -> this[i + 1, j]
            }
        }
    }

    fun <U : T> addColumn(col: Int, f: (Int) -> U): Matrix<T> {
        require(col in 0..nbColumns) { "column must be within bounds" }
        return Matrix(nbRows, nbColumns + 1) { i, j ->
            when {
                j < col -> this[i, j]
                j == col -> f(i)
                else -> this[i, j - 1]
            }
        }
    }

    fun deleteColumn(col: Int): Matrix<T> {
        require(col in 0 until nbColumns) { "column must be within bounds" }
        return Matrix(nbRows, nbColumns - 1) { i, j ->
            when {
                j < col -> this[i, j]
                else -> this[i, j + 1]
            }
        }
    }


    fun minor(row: Int, col: Int) =
        Matrix(nbRows - 1, nbColumns - 1) { i, j ->
            this[if (i < row) i else i + 1, if (j < col) j else j + 1]
        }

    fun <U : T> augment(m: Matrix<U>): Matrix<T> {
        require(nbRows == m.nbRows) { "Matrices must have the same number of rows" }
        return Matrix(nbRows, nbColumns + m.nbColumns) { i, j ->
            if (j >= nbColumns) m[i, j - nbColumns] else this[i, j]
        }
    }

    fun <U : T> augment(values: Array<U>) = augment(values.asList())
    fun <U : T> augment(values: List<U>): Matrix<T> {
        require(nbRows == values.size) { "Vector size must be same as number of rows in Matrix" }
        return addColumn(nbColumns) { i -> values[i] }
    }


    fun <R> map(f: (T) -> R) = Matrix(nbRows, nbColumns) { i, j -> f(this[i, j]) }
    fun <R> mapIndexed(f: (Int, Int, T) -> R) =
        Matrix(nbRows, nbColumns) { i: Int, j: Int -> f(i, j, this[i, j]) }

    fun <U : T> rowMap(row: Int, f: (T) -> U) = Matrix(this).rowTransform(row, f)
    fun <U : T> rowMapIndexed(row: Int, f: (Int, Int, T) -> U) = Matrix(this).rowTransformIndexed(row, f)

    fun <U : T> columnMap(col: Int, f: (T) -> U) = Matrix(this).columnTransform(col, f)
    fun <U : T> columnMapIndexed(col: Int, f: (Int, Int, T) -> U) = Matrix(this).columnTransformIndexed(col, f)

    fun <U : T> transform(f: (T) -> U): Matrix<T> {
        for (i in data.indices) {
            for (j in 0 until data[i].size) {
                set(i, j, f(get(i, j)))
            }
        }
        return this
    }

    fun <U : T> transformIndexed(f: (Int, Int, T) -> U): Matrix<T> {
        for (i in data.indices) {
            for (j in 0 until data[i].size) {
                set(i, j, f(i, j, get(i, j)))
            }
        }
        return this
    }

    fun <U : T> rowTransform(row: Int, f: (T) -> U): Matrix<T> {
        for (col in 0 until nbColumns) this[row, col] = f(this[row, col])
        return this
    }

    fun <U : T> rowTransformIndexed(row: Int, f: (Int, Int, T) -> U): Matrix<T> {
        for (col in 0 until nbColumns) this[row, col] = f(row, col, this[row, col])
        return this
    }

    fun <U : T> columnTransform(col: Int, f: (T) -> U): Matrix<T> {
        for (row in 0 until nbRows) this[row, col] = f(this[row, col])
        return this
    }

    fun <U : T> columnTransformIndexed(col: Int, f: (Int, Int, T) -> U): Matrix<T> {
        for (row in 0 until nbColumns) this[row, col] = f(row, col, this[row, col])
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Matrix<*>) return false

        if (nbRows != other.nbRows) return false
        if (nbColumns != other.nbColumns) return false
        if (!data.contentDeepEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = data.contentDeepHashCode()
        result = 31 * result + nbRows
        result = 31 * result + nbColumns
        return result
    }

    override fun toString(): String {
        val joiner = StringJoiner("\n")
        data.forEach { row ->
            val innerJoiner = StringJoiner(", ")
            row.forEach { innerJoiner.add(it.toString()) }
            joiner.add(innerJoiner.toString())
        }
        return joiner.toString()
    }
}


