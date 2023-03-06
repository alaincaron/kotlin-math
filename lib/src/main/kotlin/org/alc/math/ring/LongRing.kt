package org.alc.math.ring

object LongRing : Ring<Long>, Comparator<Long> {
    override fun zero() = 0L
    override fun one() = 1L
    override fun negate(a: Long) = -a
    override fun multiply(a: Long, b: Long) = a * b
    override fun subtract(a: Long, b: Long) = a - b
    override fun add(a: Long, b: Long) = a + b
    override fun compare(o1: Long, o2: Long) = o1.compareTo(o2)
}
