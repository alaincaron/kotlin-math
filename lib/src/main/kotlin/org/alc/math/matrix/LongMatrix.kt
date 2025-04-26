package org.alc.math.matrix

import org.alc.math.ring.LongRing
import org.alc.util.matrix.Matrix

object LongMatrix : RingMatrixFactory<Long>(LongRing)

operator fun Matrix<Long>.plus(other: Matrix<Long>) = LongMatrix.add(this, other)
operator fun Matrix<Long>.minus(other: Matrix<Long>) = LongMatrix.subtract(this, other)
operator fun Matrix<Long>.times(other: Matrix<Long>) = LongMatrix.multiply(this, other)

operator fun Matrix<Long>.plusAssign(other: Matrix<Long>) {
    LongMatrix.plusAssign(this, other)
}

operator fun Matrix<Long>.minusAssign(other: Matrix<Long>) {
    LongMatrix.minusAssign(this, other)
}

operator fun Matrix<Long>.unaryMinus() = LongMatrix.negate(this)

operator fun Matrix<Long>.unaryPlus() = LongMatrix(this)
