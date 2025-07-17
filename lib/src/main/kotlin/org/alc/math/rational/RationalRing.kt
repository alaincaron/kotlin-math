package org.alc.math.rational

import org.alc.math.ring.DefaultOrderedDivisionRing

object RationalRing : DefaultOrderedDivisionRing<Rational>, Comparator<Rational> {
    override fun zero() = Rational.ZERO
    override fun one() = Rational.ONE
    override fun nan() = Rational.NaN
    override fun compare(o1: Rational, o2: Rational) = o1.compareTo(o2)
}

