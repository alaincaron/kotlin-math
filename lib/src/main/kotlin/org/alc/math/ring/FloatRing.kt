package org.alc.math.ring

object FloatRing : DivisionRing<Float>, Comparator<Float> {
    override fun zero() = 0.0f
    override fun one() = 1.0f
    override fun negate(a: Float) = -a
    override fun multiply(a: Float, b: Float) = a * b
    override fun subtract(a: Float, b: Float) = a - b
    override fun add(a: Float, b: Float) = a + b
    override fun divide(a: Float, b: Float) = a / b
    override fun compare(o1: Float, o2: Float) = o1.compareTo(o2)
}
