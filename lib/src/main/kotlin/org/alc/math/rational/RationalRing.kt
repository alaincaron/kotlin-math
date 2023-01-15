package org.alc.math.rational

import org.alc.math.ring.DefaultDivisionRing

class RationalRing : DefaultDivisionRing<Rational>(), Comparator<Rational> {
    override fun zero() = Rational.ZERO
    override fun one() = Rational.ONE
    override fun compare(o1: Rational, o2: Rational) = o1.compareTo(o2)
}

