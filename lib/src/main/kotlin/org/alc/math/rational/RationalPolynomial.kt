package org.alc.math.rational

import org.alc.math.Point2d
import org.alc.math.matrix.GaussianElimination
import org.alc.math.matrix.RationalMatrix
import java.lang.Integer.max
import java.util.function.Function
import kotlin.math.abs
import kotlin.math.sqrt

class RationalPolynomial private constructor(val coefficients: List<Rational>) : Function<Rational, Rational> {

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
        return invoke(p)
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
        return invoke(p)
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
        return invoke(result)
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
        return Pair(invoke(q), invoke(r))
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

    fun derivative(): RationalPolynomial {
        val n = this.degree()
        if (n == 0) return ZERO
        val d = mutableListOf<Rational>()
        var i = 0
        while (i < n) {
            d.add((n - i) * coefficients[i])
            ++i
        }
        return invoke(d)
    }

    fun integrate(): RationalPolynomial {
        val n = degree() + 1
        val d = MutableList(n) { i -> coefficients[i] / (n - i) }
        d.add(Rational.ZERO)
        return invoke(d)
    }


    fun rationalRoot(
        initialGuess: Rational = Rational.ONE,
        epsilon: Rational = 1 over 1000000, maxIterations: Int = 100
    ) = when (degree()) {
        0 -> Rational.NaN
        1 -> -coefficients[1] / coefficients[0]
        else -> newtonRationalRoot(initialGuess, epsilon, maxIterations)
    }

    fun root(initialGuess: Double = 1.0, epsilon: Double = 1e-6, maxIterations: Int = 1000) = when (degree()) {
        0 -> Double.NaN
        1 -> (-coefficients[1] / coefficients[0]).toDouble()
        2 -> root2()
        else -> newtonRoot(initialGuess, epsilon, maxIterations)
    }

    private fun newtonRationalRoot(
        initialGuess: Rational,
        epsilon: Rational, maxIterations: Int
    ): Rational {
        var x0 = initialGuess
        for (iter in 1..maxIterations) {
            var f = coefficients[0]
            var fPrime: Rational = Rational.ZERO
            for (i in 1..degree()) {
                fPrime = f + (x0 * fPrime)
                f = coefficients[i] + (x0 * f)
            }
            val ratio = f / fPrime
            x0 -= ratio
            if (ratio.abs() <= epsilon) break
        }
        return x0
    }


    private fun newtonRoot(initialGuess: Double, epsilon: Double, maxIterations: Int): Double {
        var x0 = initialGuess
        val c = coefficients.map { it.toDouble() }
        for (iter in 1..maxIterations) {
            var f = c[0]
            var fPrime = 0.0
            for (i in 1..degree()) {
                fPrime = f + (x0 * fPrime)
                f = c[i] + (x0 * f)
            }
            val ratio = f / fPrime
            x0 -= ratio
            if (abs(ratio) <= epsilon) break
        }
        return x0
    }

    private fun root2(): Double {
        val a = coefficients[0]
        val b = coefficients[1]
        val c = coefficients[2]
        val x = b * b - 4 * a * c

        if (x < 0) return Double.NaN
        return (-b.toDouble() + sqrt(x.toDouble())) / (2.0 * a.toDouble())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RationalPolynomial

        return coefficients == other.coefficients
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

        operator fun invoke(vararg coefficients: Number) =
            invoke(coefficients.map { it.toRational() })

        internal operator fun invoke(coefficients: List<Rational>) = when (coefficients.size) {
            0 -> ZERO
            1 -> when (coefficients[0]) {
                Rational.ZERO -> ZERO
                Rational.ONE -> ONE
                else -> canonicalValue(coefficients.asSequence())
            }

            else -> canonicalValue(coefficients.asSequence())
        }

        fun interpolate(vararg points: Point2d<Rational>) = interpolate(points.asList())

        fun interpolate(points: List<Point2d<Rational>>): RationalPolynomial {
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
            return invoke(GaussianElimination(RationalRing, m).solve())
        }

        private fun linearInterpolation(p1: Point2d<Rational>, p2: Point2d<Rational>): RationalPolynomial {
            val deltaX = p1.x - p2.x
            if (deltaX == Rational.ZERO) throw ArithmeticException("Infinite slope")
            val slope = (p1.y - p2.y) / deltaX
            val b = p1.y - p1.x * slope
            return invoke(listOf(slope, b))
        }


        private val instanceCache = mutableMapOf<List<Rational>, RationalPolynomial>()

        private fun store(p: RationalPolynomial): RationalPolynomial {
            instanceCache[p.coefficients] = p
            return p
        }

        private fun canonicalValue(coefficients: Sequence<Rational>): RationalPolynomial {
            val c = coefficients.dropWhile { it.isZero() }.toList()
            val cachedValue = instanceCache[c]
            if (cachedValue != null) return cachedValue
            return when (c.size) {
                0 -> ZERO
                else -> RationalPolynomial(c)
            }
        }

        val ZERO = store(RationalPolynomial(listOf(Rational.ZERO)))
        val ONE = store(RationalPolynomial(listOf(Rational.ONE)))
        val IDENTITY = store(RationalPolynomial(listOf(Rational.ONE, Rational.ZERO)))
        val SQUARE = store(RationalPolynomial(listOf(Rational.ONE, Rational.ZERO, Rational.ZERO)))
        val CUBE = store(RationalPolynomial(listOf(Rational.ONE, Rational.ZERO, Rational.ZERO, Rational.ZERO)))

        init {
            instanceCache[listOf()] = ZERO
        }
    }

}



