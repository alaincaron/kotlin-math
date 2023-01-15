package org.alc.math.rational

import org.alc.math.ring.DefaultRing

class RationalRing : DefaultRing<Rational>() {
    override fun zero() = Rational.ZERO
    override fun one() = Rational.ONE
}

