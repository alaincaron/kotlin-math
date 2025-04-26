package org.alc.math.matrix

import org.alc.math.ring.DivisionRing
import org.alc.math.ring.Ring
import org.alc.math.ring.RingElement
import org.alc.util.matrix.Matrix


abstract class RingMatrixFactory<T : Any>(protected val ring: Ring<T>) {

    open operator fun invoke(nbRows: Int, nbColumns: Int, value: T) =
        Matrix(nbRows, nbColumns) { _, _ -> value }

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, ring.zero())
    operator fun invoke(matrix: Matrix<T>) = Matrix(matrix)

    fun zeroes(nbRows: Int, nbColumns: Int) =
        Matrix(nbRows, nbColumns, ring.zero())

    fun identity(size: Int) =
        Matrix(size, size) { i, j -> if (i == j) ring.one() else ring.zero() }

    fun negate(a: Matrix<T>) =
        Matrix(a.nbRows, a.nbColumns) { i, j -> ring.negate(a[i, j]) }

    fun multiply(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        require(a.compatibleForMultiplication(b))
        return Matrix(a.nbRows, b.nbColumns) { i, j ->
            var sum = ring.multiply(a[i, 0], b[0, j])
            for (k in 1 until b.nbColumns) {
                sum = ring.add(sum, ring.multiply(a[i, k], b[k, j]))
            }
            sum
        }
    }

    fun subtract(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        a.requireSameDimensions(b)
        return Matrix(a.nbRows, b.nbColumns) { i, j -> ring.subtract(a[i, j], b[i, j]) }
    }

    fun add(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        a.requireSameDimensions(b)
        return Matrix(a.nbRows, b.nbColumns) { i, j -> ring.add(a[i, j], b[i, j]) }
    }

    fun plusAssign(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        a.requireSameDimensions(b)
        return a.transformIndexed { i, j, v -> ring.add(v, b[i, j]) }
    }

    fun minusAssign(a: Matrix<T>, b: Matrix<T>): Matrix<T> {
        a.requireSameDimensions(b)
        return a.transformIndexed { i, j, v -> ring.subtract(v, b[i, j]) }
    }

    fun augmentIdentity(m: Matrix<T>) =
        Matrix(m.nbRows, m.nbColumns + m.nbRows) { i, j ->
            when (j) {
                in 0 until m.nbColumns -> m[i, j]
                i + m.nbColumns -> ring.one()
                else -> ring.zero()
            }
        }
}

abstract class InvertibleMatrixFactory<T : Comparable<T>>(ring: DivisionRing<T>) : RingMatrixFactory<T>(ring) {
    fun invert(m: Matrix<T>): Matrix<T> {
        val work = invertBase(m)
        return Matrix(m.nbRows, m.nbColumns) { i, j -> work[i, j + m.nbColumns] }
    }

    private fun invertBase(m: Matrix<T>): Matrix<T> {
        m.requireIsSquare()
        val work = augmentIdentity(m)
        GaussianElimination(ring as DivisionRing<T>, work).invert()
        return work
    }

    fun determinant(m: Matrix<T>): T {
        m.requireIsSquare()
        val work = Matrix(m)
        val gauss = GaussianElimination(ring as DivisionRing<T>, work)
        return gauss.determinant()
    }

    fun invertTransform(m: Matrix<T>): Matrix<T> {
        val work = invertBase(m)
        return m.transformIndexed { i, j, _ -> work[i, j + m.nbColumns] }
    }

    fun solve(m: Matrix<T>, values: List<T>): List<T> {
        require(m.nbRows == values.size) { "Matrix and values must have same number of rows" }
        val work = m.augment(values)
        val gauss = GaussianElimination(ring as DivisionRing<T>, work)
        return gauss.solve()
    }

}


operator fun <T : RingElement<T>> Matrix<T>.plus(other: Matrix<T>): Matrix<T> {
    require(sameDimensions(other)) { "Matrices must be of same dimension" }
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] + other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.minus(other: Matrix<T>): Matrix<T> {
    requireSameDimensions(other)
    return Matrix(nbRows, nbColumns) { i, j -> this[i, j] - other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.times(other: Matrix<T>): Matrix<T> {
    require(compatibleForMultiplication(other))
    return Matrix(nbRows, other.nbColumns) { i, j ->
        var sum = this[i, 0] * other[0, j]
        for (k in 1 until this.nbColumns) {
            sum += this[i, k] * other[k, j]
        }
        sum
    }
}

operator fun <T : RingElement<T>> Matrix<T>.plusAssign(other: Matrix<T>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v + other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.minusAssign(other: Matrix<T>) {
    requireSameDimensions(other)
    transformIndexed { i, j, v -> v - other[i, j] }
}

operator fun <T : RingElement<T>> Matrix<T>.unaryMinus() =
    Matrix(nbRows, nbColumns) { i, j -> -this[i, j] }

operator fun <T : RingElement<T>> Matrix<T>.unaryPlus() = Matrix(this)


operator fun <T : RingElement<T>> Matrix<T>.times(v: Array<T>): Matrix<T> {
    require(nbColumns == v.size) { "Matrix and vector are not compatible for multiplication" }
    return Matrix(nbRows, nbColumns) { i, _ ->
        var sum = this[i, 0] * v[0]
        for (k in 1 until v.size) {
            sum += this[i, k] * v[k]
        }
        sum
    }
}

operator fun <T : RingElement<T>> Array<T>.times(m: Matrix<T>): Matrix<T> {
    require(size == m.nbRows) { "Matrix and vector are not compatible for multiplication" }
    return Matrix(m.nbRows, m.nbColumns) { i, j ->
        var sum = this[0] * m[0, i]
        for (k in 1 until size) {
            sum += this[k] * m[k, j]
        }
        sum
    }
}
