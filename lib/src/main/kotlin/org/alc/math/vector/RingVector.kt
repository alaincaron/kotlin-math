package org.alc.math.vector

import org.alc.math.rational.Rational
import org.alc.math.ring.DivisionRing
import org.alc.math.ring.DivisionRingElement
import org.alc.math.ring.Ring
import org.alc.math.ring.RingElement
import java.util.Collection as JavaCollection


fun requireSameSize(a: Array<*>, b: Array<*>) {
    require(a.size == b.size) { "Arrays must be of same size" }
}

abstract class RingVectorFactory<T : Any>(protected val ring: Ring<T>, protected val klass: Class<T>) {

    open operator fun invoke(size: Int, f: (Int) -> T) = convert(List(size,f))
    open operator fun invoke(size: Int, value: T) = convert(List(size){value})
    open operator fun invoke(size: Int) = invoke(size, ring.zero())

    protected fun convert(list: List<T>) =
        (list as JavaCollection<T>).toArray(
            java.lang.reflect.Array.newInstance(klass, 0) as Array<T>
        ) as Array<T>

    fun zero(size: Int) = convert(List(size) { ring.zero() })

    fun negate(a: Array<T>) = a.clone().transform { ring.negate(it) }

    fun multiply(a: Array<T>, b: Array<T>): T {
        requireSameSize(a, b)
        var sum = ring.zero()
        for (i in a.indices) {
            sum = ring.add(sum, ring.multiply(a[i], b[i]))
        }
        return sum
    }

    fun multiply(a: Array<T>, b: T) = a.clone().transform { ring.multiply(it, b) }
    fun multiply(a: T, b: Array<T>) = b.clone().transform { ring.multiply(it, a) }

    fun subtract(a: Array<T>, b: Array<T>): Array<T> {
        requireSameSize(a, b)
        return convert(List(a.size) { i -> ring.subtract(a[i], b[i]) })
    }

    fun add(a: Array<T>, b: Array<T>): Array<T> {
        requireSameSize(a, b)
        return convert(List(a.size) { i -> ring.add(a[i], b[i]) })
    }


    fun normSquare(a: Array<T>) = multiply(a, a)
    fun dot(a: Array<T>, b: Array<T>): T = multiply(a, b)

    fun cross(a: Array<T>, b: Array<T>): Array<T> {
        require(a.size == b.size && a.size == 3) { "Arrays must be of size 3" }
        return convert(List(3) {
            when (it) {
                0 -> ring.subtract(ring.multiply(a[1], b[2]), ring.multiply(a[2], b[1]))
                1 -> ring.subtract(ring.multiply(a[0], b[2]), ring.multiply(a[0], b[2]))
                else -> ring.subtract(ring.multiply(a[0], b[1]), ring.multiply(a[1], b[0]))
            }
        })
    }
}

abstract class DivisionRingVectorFactory<T : Any>
    (ring: DivisionRing<T>, klass: Class<T>) : RingVectorFactory<T>(ring, klass) {

    fun projection(a: Array<T>, base: Array<T>) =
        multiply(
            (ring as DivisionRing<T>).divide(multiply(a, base), normSquare(base)),
            base
        )
}


inline operator fun <reified T : RingElement<T>> Array<T>.plus(other: Array<T>): Array<T> {
    requireSameSize(this, other)
    return Array(size) { i -> this[i] + other[i] }
}

inline operator fun <reified T : RingElement<T>> Array<T>.minus(other: Array<T>): Array<T> {
    requireSameSize(this, other)
    return Array(size) { i -> this[i] - other[i] }
}

operator fun <T : RingElement<T>> Array<T>.times(other: Array<T>): T {
    requireSameSize(this, other)
    require(isNotEmpty())
    var sum = this[0] * other[0]
    for (i in 1 until size) sum += this[i] * other[i]
    return sum
}

inline operator fun <reified T : RingElement<T>> Array<T>.times(other: T): Array<T> {
    return Array(size) { i -> this[i] * other }
}

inline operator fun <reified T : RingElement<T>> T.times(other: Array<T>) =
    Array(other.size) { i -> this * other[i] }

inline operator fun <reified T : RingElement<T>> Array<T>.unaryPlus() = clone()
inline operator fun <reified T : RingElement<T>> Array<T>.unaryMinus() = Array(size) { i -> -this[i] }


fun <T : RingElement<T>> Array<T>.normSquare() = this * this

infix fun <T : RingElement<T>> Array<T>.dot(other: Array<T>): T = this * other

inline infix fun <reified T : RingElement<T>> Array<T>.cross(other: Array<T>): Array<T> {
    require(size == other.size && size == 3) { "Arrays must be of size 3" }
    return Array(3) {
        when (it) {
            0 -> (get(1) * other[2]) - (get(2) * other[1])
            1 -> (get(2) * other[0]) - (get(0) * other[2])
            else -> (get(0) * other[1]) - (get(1) * other[0])
        }
    }
}

inline infix fun <reified T : DivisionRingElement<T>> Array<T>.project(base: Array<T>) =
    ((this * base) / base.normSquare()) * base
