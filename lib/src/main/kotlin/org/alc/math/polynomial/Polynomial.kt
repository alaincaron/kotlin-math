package org.alc.math.polynomial

import org.alc.math.Point2d
import org.alc.math.fix0
import org.alc.math.matrix.DoubleMatrix
import org.alc.math.ring.RemainderRingElement
import java.lang.Integer.max
import java.util.function.Function
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

open class Polynomial internal constructor(val coefficients: List<Double>) : Function<Double, Double>,
    RemainderRingElement<Polynomial> {

    override fun apply(x: Double) = coefficients.fold(0.0) { sum, v -> sum * x + v }
    fun degree() = coefficients.size - 1

    override operator fun plus(other: Polynomial): Polynomial {
        if (this == ZERO) return other
        if (other == ZERO) return this
        val d = max(this.degree(), other.degree())
        val d1 = d - this.degree()
        val d2 = d - other.degree()
        val p = mutableListOf<Double>()
        for (i in 0..d) {
            val i1 = i - d1
            val i2 = i - d2
            val c1 = if (i1 < 0) 0.0 else coefficients[i1]
            val c2 = if (i2 < 0) 0.0 else other.coefficients[i2]
            p.add(c1 + c2)
        }
        return create(p)
    }

    override operator fun minus(other: Polynomial): Polynomial {
        if (other == ZERO) return this
        if (this == ZERO) return -other
        if (other == this) return ZERO
        val d = max(this.degree(), other.degree())
        val d1 = d - this.degree()
        val d2 = d - other.degree()
        val p = mutableListOf<Double>()
        for (i in 0..d) {
            val i1 = i - d1
            val i2 = i - d2
            val c1 = if (i1 < 0) 0.0 else coefficients[i1]
            val c2 = if (i2 < 0) 0.0 else other.coefficients[i2]
            p.add(c1 - c2)
        }
        return create(p)
    }

    override operator fun times(other: Polynomial): Polynomial {
        if (this == ZERO || other == ZERO) return ZERO
        if (this == ONE) return other
        if (other == ONE) return this
        val d1 = this.degree()
        val d2 = other.degree()
        val result = MutableList(d1 + d2 + 1) { 0.0 }

        for (i in 0..d1) {
            for (j in 0..d2) {
                result[i + j] += this.coefficients[i] * other.coefficients[j]
            }
        }
        return create(result)
    }

    operator fun times(other: Double) = when (other) {
        0.0 -> ZERO
        1.0 -> this
        else -> canonicalValue(coefficients.asSequence().map { it * other })
    }

    fun divideAndRemainder(den: Polynomial): Pair<Polynomial, Polynomial> {
        val q = mutableListOf<Double>()
        val r = this.coefficients.toMutableList()
        val d = den.coefficients

        var i = 1
        while (r.size >= d.size) {
            val t = r[0] / d[0]
            q.add(t)
            d.forEachIndexed { idx, v -> r[idx] -= v * t }
            r.removeAt(0)
            i += 1
        }
        return Pair(create(q), create(r))
    }

    override operator fun div(den: Polynomial) = divideAndRemainder(den).first
    override operator fun rem(den: Polynomial) = divideAndRemainder(den).second

    operator fun div(den: Double) = canonicalValue(coefficients.asSequence().map { it / den })

    operator fun unaryPlus() = this

    operator fun unaryMinus() = when (this) {
        ZERO -> this
        else -> canonicalValue(coefficients.asSequence().map { -it })
    }

    fun negate() = unaryMinus()

    open fun derivative(): Polynomial {
        val n = this.degree()
        if (n == 0) return ZERO
        val d = mutableListOf<Double>()
        var i = 0
        while (i < n) {
            d.add((n - i) * coefficients[i])
            ++i
        }
        return create(d)
    }


    open fun root(initial_guess: Double = 1.0, epsilon: Double = 1e-6, max_iterations: Int = 1000): Double {
        var x0 = initial_guess
        for (iter in 1..max_iterations) {
            var f = coefficients[0]
            var f_prime = 0.0
            for (i in 1..degree()) {
                f_prime = f + (x0 * f_prime)
                f = coefficients[i] + (x0 * f)
            }
            val ratio = f / f_prime
            x0 -= ratio
            if (abs(ratio) <= epsilon) break
        }
        return x0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Polynomial

        if (coefficients != other.coefficients) return false

        return true
    }

    override fun hashCode(): Int {
        return coefficients.hashCode()
    }

    override fun toString(): String {
        val builder = StringBuilder()
        var first = true
        for (i in 0..degree()) {
            val c = coefficients[i]
            if (c == 0.0) {
                if (first) {
                    first = false
                    builder.append('0')
                }
                continue
            }
            if (first) {
                first = false
                if (c < 0.0) builder.append('-')
            } else {
                builder.append(' ')
                builder.append(if (c < 0.0) '-' else '+')
                builder.append(' ')
            }
            val r = abs(c)
            val exp = degree() - i
            val z = round(r)
            if (z == r) {
                val k = z.toInt()
                if (k != 1 || exp == 0) builder.append(k)
            } else {
                if (r != 1.0 || exp == 0) builder.append(r)
            }
            when (exp) {
                0 -> continue
                1 -> builder.append("x")
                else -> builder.append("x^").append(exp)
            }
        }
        return builder.toString()
    }


    companion object {
        fun create(vararg coefficients: Double) = create(
            mutableListOf<Double>().also { it.addAll(coefficients.asList()) }
        )

        fun create(vararg coefficients: Number) = create(coefficients.map { it.toDouble() })

        internal fun create(coefficients: List<Double>) = when (coefficients.size) {
            0 -> ZERO
            1 -> when (coefficients[0]) {
                0.0 -> ZERO
                1.0 -> ONE
                else -> canonicalValue(coefficients.asSequence())
            }

            else -> nonTrivialList(coefficients)
        }

        fun interpolate(vararg points: Point2d<Double>) = interpolate(points.asList())

        fun interpolate(points: List<Point2d<Double>>): Polynomial {
            require(points.size >= 2) { "Interpolation requires at least 2 points" }
            if (points.size == 2) return linearInterpolation(points[0], points[1])

            val m = DoubleMatrix(points.size, points.size + 1, 1.0)
            for (i in points.indices) {
                m[i, points.size] = points[i].y
                val v = points[i].x
                var c = v
                for (j in points.size - 2 downTo 0) {
                    m[i, j] = c
                    c *= v
                }
            }
            return create(DoubleMatrix.GaussianSolver(m).solve().asList())
        }

        private fun linearInterpolation(p1: Point2d<Double>, p2: Point2d<Double>): Polynomial {
            val delta_x = p1.x - p2.x
            if (delta_x == 0.0) throw ArithmeticException("Infinite slope")
            val slope = (p1.y - p2.y) / delta_x
            val b = p1.y - p1.x * slope
            return create(listOf(slope, b))
        }

        private fun nonTrivialList(coefficients: List<Double>): Polynomial {
            val c = coefficients.asSequence().dropWhile { x -> x == 0.0 }
            return canonicalValue(c)
        }

        private val instanceCache = mutableMapOf<List<Double>, Polynomial>()

        private fun <T : Polynomial> store(t: T): T {
            instanceCache[t.coefficients] = t
            return t
        }

        private fun canonicalValue(coefficients: Sequence<Double>): Polynomial {
            val c = coefficients.map(::fix0).toList()
            val cachedValue = instanceCache[c]
            if (cachedValue != null) return cachedValue
            return when (c.size) {
                0 -> ZERO
                1 -> Constant(c)
                2 -> Linear(c)
                3 -> Quadratic(c)
                else -> Polynomial(c)
            }
        }

        val ZERO = store(Constant(0.0))
        val ONE = store(Constant(1.0))
        val IDENTITY = store(Linear(listOf(1.0, 0.0)))
        val SQUARE = store(Quadratic(listOf(1.0, 0.0, 0.0)))
        val CUBE = store(Polynomial(listOf(1.0, 0.0, 0.0, 0.0)))

        init {
            instanceCache[listOf()] = ZERO
        }
    }

    class Constant internal constructor(coefficients: List<Double>) : Polynomial(coefficients) {
        val value get() = coefficients[0]

        internal constructor(coefficient: Double) : this(listOf(coefficient))

        override fun root(initial_guess: Double, epsilon: Double, max_iterations: Int) = Double.NaN
        override fun derivative(): Constant = ZERO
    }

    class Linear internal constructor(coefficients: List<Double>) : Polynomial(coefficients) {
        val slope get() = coefficients[0]

        override fun root(initial_guess: Double, epsilon: Double, max_iterations: Int) = -coefficients[1] / coefficients[0]
        override fun derivative() = create(listOf(slope)) as Constant
    }

    class Quadratic internal constructor(coefficients: List<Double>) : Polynomial(coefficients) {
        val a get() = coefficients[0]
        val b get() = coefficients[1]
        val c get() = coefficients[2]

        val extremum get() = -b / 2 / a
        override fun derivative() = create(listOf(2.0 * a, b)) as Linear

        override fun root(initial_guess: Double, epsilon: Double, max_iterations: Int): Double {
            val x = b * b - 4.0 * a * c
            if (x < 0.0) return Double.NaN
            return (-b + sqrt(x)) / (2.0 * a)
        }
    }
}



