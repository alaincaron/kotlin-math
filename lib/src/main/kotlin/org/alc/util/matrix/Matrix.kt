package org.alc.util.matrix

sealed class AbstractMatrix<T> constructor(protected val data: Array<Array<Any?>>) {

    @Suppress("UNCHECKED_CAST")
    operator fun get(i: Int, j: Int): T {
        val x = data[i]
        return x[j] as T
    }

    companion object {
        internal fun <T> create(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T): Array<Array<Any?>> {
            return Array(nbRows) { i ->
                Array(nbColumns) { j -> f(i, j) as Any }
            }
        }
    }
}

open class MutableMatrix<T> protected constructor(data: Array<Array<Any?>>) :
    AbstractMatrix<T>(data) {
    operator fun set(i: Int, j: Int, value: T) {
        data[i][j] = value
    }

    companion object {
        operator fun <T> invoke(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) =
            MutableMatrix<T>(create(nbRows, nbColumns, f))
    }
}

open class Matrix<T> protected constructor(data: Array<Array<Any?>>) :
    AbstractMatrix<T>(data) {

    companion object {
        operator fun <T> invoke(nbRows: Int, nbColumns: Int, f: (Int, Int) -> T) =
            Matrix<T>(create(nbRows, nbColumns, f))
    }

}
