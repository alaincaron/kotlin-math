package org.alc.math.rational

import org.alc.math.Point2D
import org.alc.math.matrix.RationalMatrix
import java.lang.Integer.max
import java.util.function.Function
import kotlin.math.abs
import kotlin.math.sqrt

class ConstantRationalPolynomial internal constructor(coefficients: List<Rational>) : RationalPolynomial(coefficients) {

    val value get() = coefficients[0]

    internal constructor(coefficient: Rational) : this(listOf(coefficient))

    override fun root(initial_guess: Double, epsilon: Double, max_iterations: Int) = Double.NaN
    override fun rationalRoot(initial_guess: Rational, epsilon: Rational, max_iterations: Int) = Rational.NaN

    override fun derivative(): ConstantRationalPolynomial = ZERO
}

 class LinearRationalPolynomial internal constructor(coefficients: List<Rational>) : RationalPolynomial(coefficients) {
    val m get() = coefficients[0]
    val b get() = coefficients[1]

    override fun rationalRoot(initial_guess: Rational, epsilon: Rational, max_iterations: Int) = -b / m

    override fun root(initial_guess: Double, epsilon: Double, max_iterations: Int) = (-b / m).toDouble()

    override fun derivative() = withCoefficients(m) as ConstantRationalPolynomial
}

 class QuadraticRationalPolynomial internal constructor(coefficients: List<Rational>) : RationalPolynomial(coefficients) {

    val a get() = coefficients[0]
    val b get() = coefficients[1]
    val c get() = coefficients[2]

    val extremum get() = -b / 2 / a
    override fun derivative() = withCoefficients(2 * a, b) as LinearRationalPolynomial

    override fun root(initial_guess: Double, epsilon: Double, max_iterations: Int): Double {
        val x = b * b - 4 * a * c
        if (x < 0) return Double.NaN
        return (-b.toDouble() + sqrt(x.toDouble())) / (2.0 * a.toDouble())
    }
}

open class RationalPolynomial internal constructor(val coefficients: List<Rational>) : Function<Rational, Rational> {

    override fun apply(x: Rational) = coefficients.fold(Rational.ZERO) { sum, v -> sum * x + v }
    fun degree() = coefficients.size - 1

    operator fun plus(other: RationalPolynomial): RationalPolynomial {
        if (this == ZERO) return other
        if (other == ZERO) return this
        val d = max(this.degree(), other.degree())
        val d1 = d - this.degree()
        val d2 = d - other.degree()
        val p = mutableListOf<Rational>()
        for (i in 0..d) {
            val i1 = i - d1
            val i2 = i - d2
            val c1 = if (i1 < 0) Rational.ZERO else coefficients[i1]
            val c2 = if (i2 < 0) Rational.ZERO else other.coefficients[i2]
            p.add(c1 + c2)
        }
        return fromRationals(p)
    }

    operator fun minus(other: RationalPolynomial): RationalPolynomial {
        if (other == ZERO) return this
        if (this == ZERO) return -other
        if (other == this) return ZERO
        val d = max(this.degree(), other.degree())
        val d1 = d - this.degree()
        val d2 = d - other.degree()
        val p = mutableListOf<Rational>()
        for (i in 0..d) {
            val i1 = i - d1
            val i2 = i - d2
            val c1 = if (i1 < 0) Rational.ZERO else coefficients[i1]
            val c2 = if (i2 < 0) Rational.ZERO else other.coefficients[i2]
            p.add(c1 - c2)
        }
        return fromRationals(p)
    }

    operator fun times(other: RationalPolynomial): RationalPolynomial {
        if (this == ZERO || other == ZERO) return ZERO
        if (this == ONE) return other
        if (other == ONE) return this
        val d1 = this.degree()
        val d2 = other.degree()
        val result = MutableList(d1 + d2 + 1) { Rational.ZERO }

        for (i in 0..d1) {
            for (j in 0..d2) {
                result[i + j] += this.coefficients[i] * other.coefficients[j]
            }
        }
        return fromRationals(result)
    }

    operator fun times(other: Rational) = when (other) {
        Rational.ZERO -> ZERO
        Rational.ONE -> this
        else -> canonicalValue(coefficients.asSequence().map { it * other })
    }

    fun divideAndRemainder(den: RationalPolynomial): Pair<RationalPolynomial, RationalPolynomial> {
        val q = mutableListOf<Rational>()
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
        return Pair(fromRationals(q), fromRationals(r))
    }

    operator fun div(den: RationalPolynomial) = divideAndRemainder(den).first
    operator fun rem(den: RationalPolynomial) = divideAndRemainder(den).second

    operator fun div(den: Rational) = canonicalValue(coefficients.asSequence().map { it / den })

    operator fun unaryPlus() = this

    operator fun unaryMinus() = when (this) {
        ZERO -> this
        else -> canonicalValue(coefficients.asSequence().map { -it })
    }

    fun negate() = unaryMinus()

    open fun derivative(): RationalPolynomial {
        val n = this.degree()
        if (n == 0) return ZERO
        val d = mutableListOf<Rational>()
        var i = 0
        while (i < n) {
            d.add((n - i) * coefficients[i])
            ++i
        }
        return fromRationals(d)
    }

    open fun rationalRoot(
        initial_guess: Rational = Rational.ONE,
        epsilon: Rational = 1 over 1000000, max_iterations: Int = 1000
    ): Rational {
        var x0 = initial_guess
        for (iter in 1..max_iterations) {
            var f = coefficients[0]
            var f_prime = Rational.ZERO
            for (i in 1..degree()) {
                f_prime = f + (x0 * f_prime)
                f = coefficients[i] + (x0 * f)
            }
            val ratio = f / f_prime
            x0 -= ratio
            if (ratio.abs() <= epsilon) break
        }
        return x0
    }


    open fun root(initial_guess: Double = 1.0, epsilon: Double = 1e-6, max_iterations: Int = 1000): Double {
        var x0 = initial_guess
        val c = coefficients.map { it.toDouble() }
        for (iter in 1..max_iterations) {
            var f = c[0]
            var f_prime = 0.0
            for (i in 1..degree()) {
                f_prime = f + (x0 * f_prime)
                f = c[i] + (x0 * f)
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

        other as RationalPolynomial

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
            if (c == Rational.ZERO) {
                if (first) {
                    first = false
                    builder.append('0')
                }
                continue
            }
            if (first) {
                first = false
                if (c < Rational.ZERO) builder.append('-')
            } else {
                builder.append(' ')
                builder.append(if (c < Rational.ZERO) '-' else '+')
                builder.append(' ')
            }
            val r = c.abs()
            val exp = degree() - i
            if (r != Rational.ONE || exp == 0) builder.append(r)
            when (exp) {
                0 -> continue
                1 -> builder.append("x")
                else -> builder.append("x^").append(exp)
            }
        }
        return builder.toString()
    }


    companion object {

        fun withCoefficients(vararg coefficients: Number) =
            fromRationals(coefficients.asSequence().map { it.toRational() }.toList())

        fun withCoefficients(vararg coefficients: Rational) = fromRationals(coefficients.asList())

        fun fromRationals(coefficients: List<Rational>) = when (coefficients.size) {
            0 -> ZERO
            1 -> when (coefficients[0]) {
                Rational.ZERO -> ZERO
                Rational.ONE -> ONE
                else -> canonicalValue(coefficients.asSequence())
            }

            else -> nonTrivialList(coefficients)
        }

        fun interpolate(vararg points: Point2D<Rational>) = interpolate(points.asList())

        fun interpolate(points: List<Point2D<Rational>>): RationalPolynomial {
            require(points.size >= 2) { "Interpolation requires at least 2 points" }
            if (points.size == 2) return linearInterpolation(points[0], points[1])

            val m = RationalMatrix(points.size, points.size + 1, Rational.ONE)
            for (i in points.indices) {
                m[i, points.size] = points[i].y
                val v = points[i].x
                var c = v
                for (j in points.size - 2 downTo 0) {
                    m[i, j] = c
                    c *= v
                }
            }
            return fromRationals(RationalMatrix.GaussianSolver(m).solve().asList())
        }

        private fun linearInterpolation(p1: Point2D<Rational>, p2: Point2D<Rational>): RationalPolynomial {
            val delta_x = p1.x - p2.x
            if (delta_x == Rational.ZERO) throw ArithmeticException("Infinite slope")
            val slope = (p1.y - p2.y) / delta_x
            val b = p1.y - p1.x * slope
            return fromRationals(listOf(slope, b))
        }


        private fun nonTrivialList(coefficients: List<Rational>): RationalPolynomial {
            val c = coefficients.asSequence().dropWhile { x -> x == Rational.ZERO }
            return canonicalValue(c)
        }

        private val instanceCache = mutableMapOf<List<Rational>, RationalPolynomial>()

        private fun <T : RationalPolynomial> store(t: T): T {
            instanceCache[t.coefficients] = t
            return t
        }

        private fun canonicalValue(coefficients: Sequence<Rational>): RationalPolynomial {
            val c = coefficients.toList()
            val cachedValue = instanceCache[c]
            if (cachedValue != null) return cachedValue
            return when (c.size) {
                0 -> ZERO
                1 -> ConstantRationalPolynomial(c)
                2 -> LinearRationalPolynomial(c)
                3 -> QuadraticRationalPolynomial(c)
                else -> RationalPolynomial(c)
            }
        }

        val ZERO = store(ConstantRationalPolynomial(Rational.ZERO))
        val ONE = store(ConstantRationalPolynomial(Rational.ONE))
        val IDENTITY = store(LinearRationalPolynomial(listOf(Rational.ONE, Rational.ZERO)))
        val SQUARE = store(QuadraticRationalPolynomial(listOf(Rational.ONE, Rational.ZERO, Rational.ZERO)))
        val CUBE = store(RationalPolynomial(listOf(Rational.ONE, Rational.ZERO, Rational.ZERO, Rational.ZERO)))

        init {
            instanceCache[listOf()] = ZERO
        }
    }

}



