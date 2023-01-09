package org.alc.util.matrix

abstract class AbstractMatrix<T>  {
    internal val data: Array<Array<Any?>>
    val nbRows: Int
    val nbColumns: Int

    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) {
        this.nbRows = nbRows
        this.nbColumns = nbColumns
        data = create(nbRows, nbColumns, f)
    }

    constructor(values: AbstractMatrix<T>) : this(values.nbRows, values.nbColumns, { i, j -> values[i, j] })

    companion object {
        fun <T> create(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T): Array<Array<Any?>> {
            return Array(nbRows) { i ->
                Array(nbColumns) { j -> f(i, j) }
            }
        }

        fun <X> transposeFunction(matrix: AbstractMatrix<X>): (Int,Int) -> X {
            return {i: Int, j: Int -> matrix[j, i]}
        }
    }

    abstract fun transpose(): AbstractMatrix<T>

    fun <U> rowReduce(row: Int, initial: U, f: (U,T) -> U): U {
        var acc = initial
        for (column in 0 until nbColumns) {
            acc = f(acc, get(row, column))
        }
        return acc
    }

    fun <U> columnReduce(column: Int, initial: U, f: (U,T) -> U): U {
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
        return "AbstractMatrix(data=${data.contentDeepToString()}, nbRows=$nbRows, nbColumns=$nbColumns)"
    }


}

open class MutableMatrix<T> : AbstractMatrix<T> {
    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T): super(nbRows, nbColumns, f)
    constructor(nbRows: Int, nbColumns: Int, value: T) : this(nbRows, nbColumns, { _, _ -> value })
    constructor(values: AbstractMatrix<T>): super(values)
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

    override fun transpose(): MutableMatrix<T> {
       return MutableMatrix(nbColumns, nbRows, transposeFunction(this))
    }
}

open class Matrix<T>  : AbstractMatrix<T> {
    constructor(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T): super(nbRows, nbColumns, f)
    constructor(values: AbstractMatrix<T>): super(values)

    override fun transpose(): Matrix<T> {
        return Matrix(nbColumns, nbRows, transposeFunction(this))
    }
}
