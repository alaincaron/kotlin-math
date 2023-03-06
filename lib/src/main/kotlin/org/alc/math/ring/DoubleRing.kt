package org.alc.math.ring

object DoubleRing : DivisionRing<Double>, Comparator<Double> {
    override fun divide(a: Double, b: Double) = a / b
    override fun zero() = 0.0
    override fun one() = 1.0
    override fun negate(a: Double) = -a
    override fun multiply(a: Double, b: Double) = a * b
    override fun subtract(a: Double, b: Double) = a - b
    override fun add(a: Double, b: Double) = a + b

    override fun compare(o1: Double, o2: Double) = o1.compareTo(o2)
}
