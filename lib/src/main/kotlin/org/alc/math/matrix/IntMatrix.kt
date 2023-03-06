package org.alc.math.matrix

import org.alc.math.ring.IntRing
import org.alc.util.matrix.Matrix

object IntMatrix: RingMatrix<Int>(IntRing)

operator fun Matrix<Int>.plus(other: Matrix<Int>) = IntMatrix.add(this, other)
operator fun Matrix<Int>.minus(other: Matrix<Int>) = IntMatrix.subtract(this, other)
operator fun Matrix<Int>.times(other: Matrix<Int>) = IntMatrix.multiply(this, other)

operator fun Matrix<Int>.plusAssign(other: Matrix<Int>) {
    IntMatrix.plusAssign(this, other)
}

operator fun Matrix<Int>.minusAssign(other: Matrix<Int>) {
    IntMatrix.minusAssign(this, other)
}

operator fun Matrix<Int>.unaryMinus() = IntMatrix.negate(this)

operator fun Matrix<Int>.unaryPlus() = IntMatrix(this)


