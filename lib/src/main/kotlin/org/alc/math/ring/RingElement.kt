package org.alc.math.ring

interface RingElement<T : Any> {
    operator fun plus(other: T): T
    operator fun times(other: T): T
    operator fun minus(other: T): T
    operator fun unaryPlus(): T
    operator fun unaryMinus(): T
}

interface DivisionRingElement<T : Any> : RingElement<T> {
    operator fun div(den: T): T
}

