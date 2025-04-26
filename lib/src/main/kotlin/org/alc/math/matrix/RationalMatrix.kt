package org.alc.math.matrix

import org.alc.math.rational.Rational
import org.alc.math.rational.RationalRing
import org.alc.util.matrix.Matrix

object RationalMatrix : InvertibleMatrixFactory<Rational>(RationalRing)

fun Matrix<Rational>.determinant() = RationalMatrix.determinant(this)
fun Matrix<Rational>.invert() = RationalMatrix.invert(this)
fun Matrix<Rational>.invertTransform() = RationalMatrix.invertTransform(this)
fun Matrix<Rational>.solve(values: Array<Rational>) = solve(values.asList()).toTypedArray()
fun Matrix<Rational>.solve(values: List<Rational>) = RationalMatrix.solve(this, values)





