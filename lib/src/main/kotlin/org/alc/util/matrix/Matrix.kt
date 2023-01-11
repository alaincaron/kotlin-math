package org.alc.util.matrix

import java.util.*

abstract class AbstractMatrix<T> {
    internal val data: Array<Array<Any?>>
    val nbRows: Int
    val nbColumns: Int

    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) {
        this.nbRows = nbRows
        this.nbColumns = nbColumns
        data = Array(nbRows) { i -> Array(nbColumns) { j -> f(i, j) } }
    }

    constructor(values: AbstractMatrix<T>) : this(values.nbRows, values.nbColumns, { i, j -> values[i, j] })

    companion object {
        internal fun <X> transposeFunction(matrix: AbstractMatrix<X>) =
            { i: Int, j: Int -> matrix[j, i] }

        internal fun <T, R> mapFunction(f: (T) -> R, matrix: AbstractMatrix<T>) =
            { i: Int, j: Int -> f(matrix[i, j]) }

        internal fun <T, R> mapIndexedFunction(f: (Int, Int, T) -> R, matrix: AbstractMatrix<T>) =
            { i: Int, j: Int -> f(i, j, matrix[i, j]) }
    }

    abstract fun transpose(): AbstractMatrix<T>
    abstract fun <R> map(f: (T) -> R): AbstractMatrix<R>
    abstract fun <R> mapIndexed(f: (Int, Int, T) -> R): AbstractMatrix<R>

    @Suppress("UNCHECKED_CAST")
    fun <U> forEach(f: (T) -> U): Unit = data.forEach { row -> row.forEach { f(it as T) } }

    @Suppress("UNCHECKED_CAST")
    fun <U> forEachIndexed(f: (Int, Int, T) -> U): Unit =
        data.forEachIndexed { i, row -> row.forEachIndexed { j, item -> f(i, j, item as T) } }

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


    @Suppress("UNCHECKED_CAST")
    operator fun get(i: Int, j: Int): T {
        val x = data[i]
        return x[j] as T
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AbstractMatrix<*>) return false

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

open class MutableMatrix<T> : AbstractMatrix<T> {
    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) : super(nbRows, nbColumns, f)
    constructor(nbRows: Int, nbColumns: Int, value: T) : this(nbRows, nbColumns, { _, _ -> value })
    constructor(values: AbstractMatrix<T>) : super(values)

    operator fun set(i: Int, j: Int, value: T) {
        data[i][j] = value
    }

    fun swapRows(a: Int, b: Int): MutableMatrix<T> {
        if (a != b) {
            val tmp = data[a]
            data[a] = data[b]
            data[b] = tmp
        }
        return this
    }

    override fun transpose() = MutableMatrix(nbColumns, nbRows, transposeFunction(this))
    override fun <R> map(f: (T) -> R) = MutableMatrix(nbRows, nbColumns, mapFunction(f, this))
    override fun <R> mapIndexed(f: (Int, Int, T) -> R) =
        MutableMatrix(nbRows, nbColumns, mapIndexedFunction(f, this))

    fun <U : T> transform(f: (T) -> U): MutableMatrix<T> {
        for (i in 0 until data.size) {
            for (j in 0 until data[i].size) {
                set(i, j, f(get(i, j)))
            }
        }
        return this
    }

    fun <U : T> transformIndexed(f: (Int, Int, T) -> U): MutableMatrix<T> {
        for (i in 0 until data.size) {
            for (j in 0 until data[i].size) {
                set(i, j, f(i, j, get(i, j)))
            }
        }
        return this
    }
}

open class Matrix<T> : AbstractMatrix<T> {
    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) : super(nbRows, nbColumns, f)
    constructor(values: AbstractMatrix<T>) : super(values)

    override fun transpose() = Matrix(nbColumns, nbRows, transposeFunction(this))
    override fun <R> map(f: (T) -> R) = Matrix(nbRows, nbColumns, mapFunction(f, this))
    override fun <R> mapIndexed(f: (Int, Int, T) -> R) =
        Matrix(nbRows, nbColumns, mapIndexedFunction(f, this))
}
