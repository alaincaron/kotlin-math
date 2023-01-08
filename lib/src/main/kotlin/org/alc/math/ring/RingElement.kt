package org.alc.math.ring

interface RingElement<T> {
    operator fun plus(other: T): T
    operator fun times(other: T): T
    operator fun minus(other: T): T
    operator fun div(other: T): T
    operator fun rem(other: T): T
}
