package org.alc.math.ring

object IntRing : Ring<Int>, Comparator<Int> {
    override fun zero() = 0
    override fun one() = 1
    override fun negate(a: Int) = -a
    override fun multiply(a: Int, b: Int) = a * b
    override fun subtract(a: Int, b: Int) = a - b
    override fun add(a: Int, b: Int) = a + b
    override fun compare(o1: Int, o2: Int) = o1.compareTo(o2)
}
