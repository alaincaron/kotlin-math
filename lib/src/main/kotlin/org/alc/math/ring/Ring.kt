package org.alc.math.ring


interface Ring<T> {
    fun add(a: T, b: T): T
    fun multiply(a:T, b: T): T
    fun subtract(a:T, b: T): T
    fun divide(a: T, b: T): T
    fun rem(a: T, b:T): T
    fun zero(): T
    fun one(): T
}
  abstract class DefaultRing<T: RingElement<T>>: Ring<T> {
      override fun add(a: T, b: T) = a + b
      override fun multiply(a: T, b: T) = a * b
      override fun subtract(a: T, b: T) = a - b
      override fun divide(a: T, b: T) = a / b
      override fun rem(a: T, b:T) = a % b
  }

class DoubleRing: Ring<Double> {
    override fun add(a: Double, b: Double) = a + b
    override fun multiply(a: Double, b: Double) = a * b
    override fun subtract(a: Double, b: Double) = a - b
    override fun divide(a: Double, b: Double) = a / b
    override fun rem(a: Double, b: Double) = a % b
    override fun zero() = 0.0
    override fun one() = 1.0
}

