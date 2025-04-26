package org.alc.math.ring


interface Ring<T> {
    fun zero(): T
    fun one(): T
    fun add(a: T, b: T): T
    fun subtract(a: T, b: T): T
    fun multiply(a: T, b: T): T
    fun negate(a: T): T
}

interface DivisionRing<T> : Ring<T> {
    fun divide(a: T, b: T): T
}

interface DefaultRing<T : RingElement<T>> : Ring<T> {
    override fun negate(a: T) = -a
    override fun multiply(a: T, b: T) = a * b
    override fun subtract(a: T, b: T) = a - b
    override fun add(a: T, b: T) = a + b
}

interface DefaultDivisionRing<T : DivisionRingElement<T>> : DefaultRing<T>, DivisionRing<T> {
    override fun divide(a: T, b: T) = a / b
}


