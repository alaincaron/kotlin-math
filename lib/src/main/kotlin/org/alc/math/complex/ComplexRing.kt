package org.alc.math.complex

import org.alc.math.ring.DefaultDivisionRing
import org.alc.math.ring.DefaultRemainderRing

class ComplexRing : DefaultDivisionRing<Complex> {
    override fun zero() = Complex.ZERO
    override fun one() = Complex.ONE
}
