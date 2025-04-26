package org.alc.math.vector

import org.alc.math.ring.DivisionRing
import org.alc.math.ring.DivisionRingElement
import org.alc.math.ring.Ring
import org.alc.math.ring.RingElement
import org.alc.util.matrix.Matrix


fun requireSameSize(a: Array<*>, b: Array<*>) {
    require(a.size == b.size) { "Arrays must be of same size" }
}

abstract class RingVectorFactory<T : Any>(
    protected val ring: Ring<T>
) {

    open operator fun invoke(size: Int, f: (Int) -> T) = create(size, f)
    open operator fun invoke(size: Int, value: T) = create(size) { value }
    open operator fun invoke(size: Int) = create(size) { ring.zero() }

    abstract fun create(size: Int, f: (Int) -> T): Array<T>

    fun zero(size: Int) = create(size) { ring.zero() }

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
        return a.clone().transformIndexed { index, item -> ring.subtract(item, b[index]) }
    }

    fun add(a: Array<T>, b: Array<T>): Array<T> {
        requireSameSize(a, b)
        return a.clone().transformIndexed { index, item -> ring.add(item, b[index]) }
    }

    fun normSquare(a: Array<T>) = multiply(a, a)
    fun dot(a: Array<T>, b: Array<T>): T = multiply(a, b)

    fun cross(a: Array<T>, b: Array<T>): Array<T> {
        require(a.size == b.size && a.size == 3) { "Arrays must be of size 3" }
        return create(3) {
            when (it) {
                0 -> ring.subtract(ring.multiply(a[1], b[2]), ring.multiply(a[2], b[1]))
                1 -> ring.subtract(ring.multiply(a[2], b[0]), ring.multiply(a[0], b[2]))
                else -> ring.subtract(ring.multiply(a[0], b[1]), ring.multiply(a[1], b[0]))
            }
        }
    }

    fun matrixMultiply(m: Matrix<T>, v: Array<T>): Array<T> {
        require(m.nbColumns == v.size) { "Matrix and vector are not compatible for multiplication" }
        return create(m.nbRows) {
            var sum = ring.multiply(m[it, 0], v[it])
            for (k in 1 until v.size) {
                sum = ring.add(sum, ring.multiply(m[it, k], v[k]))
            }
            sum
        }
    }

    fun matrixMultiply(v: Array<T>, m: Matrix<T>): Array<T> {
        require(v.size == m.nbRows) { "Matrix and vector are not compatible for multiplication" }
        return create(m.nbColumns) {
            var sum = ring.multiply(v[it], m[0, it])
            for (k in 1 until v.size) {
                sum = ring.add(sum, ring.multiply(v[k], m[k, it]))
            }
            sum
        }
    }

    fun unaryMinus(v: Array<T>): Array<T> {
        return v.clone().transform { ring.subtract(ring.zero(), it) }
    }

    fun unaryPlus(v: Array<T>) = v.clone()
}


abstract class DivisionRingVectorFactory<T : Any>
    (ring: DivisionRing<T>) : RingVectorFactory<T>(ring) {

    protected fun ring() = ring as DivisionRing<T>

    fun projection(a: Array<T>, base: Array<T>) =
        multiply(
            ring().divide(multiply(a, base), normSquare(base)),
            base
        )

    fun divide(a: Array<T>, b: T) = a.clone().transform { ring().divide(it, b) }
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


inline operator fun <reified T : DivisionRingElement<T>> Matrix<T>.times(v: Array<T>): Array<T> {
    require(nbColumns == v.size) { "Matrix and vector are not compatible for multiplication" }
    return Array(nbRows) {
        var sum = this[it, 0] * v[0]//ring.multiply(m[it, 0], v[it])
        for (k in 1 until v.size) {
            sum += this[it, k] * v[k]
        }
        sum
    }
}

inline operator fun <reified T : DivisionRingElement<T>> Array<T>.times(m: Matrix<T>): Array<T> {
    require(size == m.nbRows) { "Matrix and vector are not compatible for multiplication" }
    return Array(m.nbColumns) {
        var sum = this[it] * m[0, it]
        for (k in 1 until size) {
            sum += this[k] * m[k, it]
        }
        sum
    }
}
