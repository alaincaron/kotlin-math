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

fun Matrix<Rational>.determinant() = GaussianSolver.determinant(RationalRing, this)
fun Matrix<Rational>.invert() = GaussianSolver.invert(RationalRing, this)
fun Matrix<Rational>.invertTransform() = GaussianSolver.invertTransform(RationalRing, this)
fun Matrix<Rational>.solve(values: Array<Rational>) = GaussianSolver.solve(RationalRing, this, values).toTypedArray()
fun Matrix<Rational>.solve(values: List<Rational>) = GaussianSolver.solve(RationalRing, this, values)





