package org.alc.math.complex

import org.alc.math.ring.DefaultDivisionRing

object ComplexRing : DefaultDivisionRing<Complex> {
    override fun zero() = Complex.ZERO
    override fun one() = Complex.ONE
    override fun nan() = Complex.NaN
}
