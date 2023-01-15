package org.alc.util.matrix

import java.util.*

class Matrix<T> {
    private val data: Array<Array<Any?>>
    val nbRows: Int
    val nbColumns: Int

    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) {
        require(nbRows > 0 && nbColumns > 0) { "rows and columns must be greater than 0"}
        this.nbRows = nbRows
        this.nbColumns = nbColumns
        data = Array(nbRows) { i -> Array(nbColumns) { j -> f(i, j) } }
    }

    constructor(values: Matrix<T>) : this(values.nbRows, values.nbColumns, { i, j -> values[i, j] })

    fun isSquare() = nbRows == nbColumns
    fun sameDimensions(other: Matrix<*>) = nbRows == other.nbRows && nbColumns == other.nbColumns
    fun compatibleForMultiplication(other: Matrix<*>) = nbColumns == other.nbRows

    fun requireIsSquare() = require(isSquare()) { "Matrix must be square"}
    fun requireSameDimensions(other: Matrix<*>) =
        require(sameDimensions(other)) { "Matrices must be of same dimensions"}
    fun requireCompatibleForMultiplication(other: Matrix<*>) =
        require(compatibleForMultiplication(other))
        { "Matrices must be compatible (this.nbColumns == other.nbRows"}

    @Suppress("UNCHECKED_CAST")
    fun <U> forEach(f: (T) -> U): Unit = data.forEach { row -> row.forEach { f(it as T) } }

    fun <U> forEachIndexed(f: (Int, Int, T) -> U): Unit =
        data.forEachIndexed { i, row -> row.forEachIndexed { j, item ->
            @Suppress("UNCHECKED_CAST")
            f(i, j, item as T)
        } }

    fun <U> rowReduce(row: Int, initial: U, f: (U, T) -> U): U {
        var acc = initial
        for (column in 0 until nbColumns) {
            acc = f(acc, get(row, column))
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


    operator fun get(i: Int, j: Int): T {
        val x = data[i]
        @Suppress("UNCHECKED_CAST")
        return x[j] as T
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



    operator fun set(i: Int, j: Int, value: T) {
        data[i][j] = value
    }

    fun swapRows(a: Int, b: Int): Matrix<T> {
        if (a != b) {
            val tmp = data[a]
            data[a] = data[b]
            data[b] = tmp
        }
        return this
    }

    fun transpose() = Matrix(nbColumns, nbRows) { i: Int, j: Int -> this[j, i] }
    fun <R> map(f: (T) -> R) = Matrix(nbRows, nbColumns) { i: Int, j: Int -> f(this[i, j]) }
    fun <R> mapIndexed(f: (Int, Int, T) -> R) =
        Matrix(nbRows, nbColumns) { i: Int, j: Int -> f(i, j, this[i, j]) }

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
}

