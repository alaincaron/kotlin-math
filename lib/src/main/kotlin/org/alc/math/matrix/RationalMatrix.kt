package org.alc.math.matrix

import org.alc.math.rational.Rational
import org.alc.math.rational.RationalRing
import org.alc.util.matrix.Matrix

object RationalMatrix {
    operator fun invoke(nbRows: Int, nbColumns: Int, value: Rational) =
        Matrix(nbRows, nbColumns, value)

    operator fun invoke(nbRows: Int, nbColumns: Int) = invoke(nbRows, nbColumns, Rational.ZERO)
    operator fun invoke(matrix: Matrix<Rational>) = Matrix(matrix)

    fun identity(size: Int) = Matrix(size, size) { i, j -> if (i == j) Rational.ONE else Rational.ZERO }

}

fun Matrix<Rational>.determinant(): Rational {
    requireIsSquare()
    val work = Matrix(this)
    val gauss = GaussianSolver(RationalRing, work)
    return gauss.determinant()
}

fun Matrix<Rational>.invert(): Matrix<Rational> {
    val work = invertBase()
    return Matrix(nbRows, nbColumns) { i, j -> work[i, j + nbColumns] }
}

fun Matrix<Rational>.invertTransform(): Matrix<Rational> {
    val work = invertBase()
    return transformIndexed { i, j, _ -> work[i, j + nbColumns] }
}

fun Matrix<Rational>.solve(values: Array<Rational>): Array<Rational> {
    require(nbRows == values.size)
    { "Matrix and values must have same number of rows" }
    val work = Matrix(
        nbRows,
        nbColumns + 1
    ) { i, j -> if (j == nbColumns) values[i] else this[i, j] }
    val gauss = GaussianSolver(RationalRing,work)
    return gauss.solve().toTypedArray()
}

private fun Matrix<Rational>.invertBase(): Matrix<Rational> {
    requireIsSquare()
    val boundary = nbColumns - 1
    val work = Matrix(nbRows, 2 * nbColumns) { i, j ->
        when (j) {
            in 0..boundary -> this[i, j]
            i + nbColumns -> Rational.ONE
            else -> Rational.ZERO
        }
    }
    GaussianSolver(RationalRing, work).invert()
    return work
}




