package org.alc.math.matrix

import org.alc.math.ring.DoubleRing
import org.alc.util.matrix.Matrix


object DoubleMatrix : InvertibleMatrixFactory<Double>(DoubleRing)

fun Matrix<Double>.determinant() = DoubleMatrix.determinant(this)

fun Matrix<Double>.invert() = DoubleMatrix.invert(this)

fun Matrix<Double>.invertTransform() = DoubleMatrix.invertTransform(this)

fun Matrix<Double>.solve(values: List<Double>) = DoubleMatrix.solve(this, values)
fun Matrix<Double>.solve(values: Array<Double>) = solve(values.asList()).toTypedArray()
fun Matrix<Double>.solve(values: DoubleArray) = solve(values.asList()).toDoubleArray()

operator fun Matrix<Double>.plus(other: Matrix<Double>) = DoubleMatrix.add(this, other)
operator fun Matrix<Double>.minus(other: Matrix<Double>) = DoubleMatrix.subtract(this, other)
operator fun Matrix<Double>.times(other: Matrix<Double>) = DoubleMatrix.multiply(this, other)

operator fun Matrix<Double>.plusAssign(other: Matrix<Double>) {
    DoubleMatrix.plusAssign(this, other)
}

operator fun Matrix<Double>.minusAssign(other: Matrix<Double>) {
    DoubleMatrix.minusAssign(this, other)
}

operator fun Matrix<Double>.unaryMinus() = DoubleMatrix.negate(this)

operator fun Matrix<Double>.unaryPlus() = DoubleMatrix(this)




