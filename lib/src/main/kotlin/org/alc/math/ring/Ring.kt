package org.alc.math.ring


interface Ring<T:Any> {
    fun add(a: T, b: T): T
    fun multiply(a: T, b: T): T
    fun subtract(a: T, b: T): T
    fun zero(): T
    fun one(): T
}

interface DivisionRing<T:Any>: Ring<T> {
    fun divide(a: T, b: T): T
    fun rem(a: T, b: T): T
}

abstract class DefaultRing<T : RingElement<T>> : Ring<T> {
    override fun add(a: T, b: T) = a + b
    override fun multiply(a: T, b: T) = a * b
    override fun subtract(a: T, b: T) = a - b
}

abstract class DefaultDivisionRing<T: DivisionRingElement<T>>: DefaultRing<T>() {
    fun divide(a: T, b: T) = a / b
    fun rem(a: T, b: T) = a % b

}

class DoubleRing : DivisionRing<Double>, Comparator<Double> {
    override fun add(a: Double, b: Double) = a + b
    override fun multiply(a: Double, b: Double) = a * b
    override fun subtract(a: Double, b: Double) = a - b
    override fun divide(a: Double, b: Double) = a / b
    override fun rem(a: Double, b: Double) = a % b
    override fun zero() = 0.0
    override fun one() = 1.0
    override fun compare(o1: Double, o2: Double) = o1.compareTo(o2)
}

