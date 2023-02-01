package org.alc.math.complex

import org.alc.math.ring.Ring

object ComplexRing : Ring<Complex> {
    override fun zero() = Complex.ZERO
    override fun one() = Complex.ONE
}
