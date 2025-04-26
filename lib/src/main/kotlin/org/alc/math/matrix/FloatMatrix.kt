package org.alc.math.matrix

import org.alc.math.ring.FloatRing
import org.alc.util.matrix.Matrix


object FloatMatrix : InvertibleMatrixFactory<Float>(FloatRing)

fun Matrix<Float>.determinant() = FloatMatrix.determinant(this)

fun Matrix<Float>.invert() = FloatMatrix.invert(this)

fun Matrix<Float>.invertTransform() = FloatMatrix.invertTransform(this)

fun Matrix<Float>.solve(values: List<Float>) = FloatMatrix.solve(this, values)
fun Matrix<Float>.solve(values: Array<Float>) = solve(values.asList()).toTypedArray()
fun Matrix<Float>.solve(values: FloatArray) = solve(values.asList()).toFloatArray()

operator fun Matrix<Float>.plus(other: Matrix<Float>) = FloatMatrix.add(this, other)
operator fun Matrix<Float>.minus(other: Matrix<Float>) = FloatMatrix.subtract(this, other)
operator fun Matrix<Float>.times(other: Matrix<Float>) = FloatMatrix.multiply(this, other)

operator fun Matrix<Float>.plusAssign(other: Matrix<Float>) {
    FloatMatrix.plusAssign(this, other)
}

operator fun Matrix<Float>.minusAssign(other: Matrix<Float>) {
    FloatMatrix.minusAssign(this, other)
}

operator fun Matrix<Float>.unaryMinus() = FloatMatrix.negate(this)

operator fun Matrix<Float>.unaryPlus() = FloatMatrix(this)




