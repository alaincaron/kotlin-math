package org.alc.math.polynomial

import java.lang.Integer.max
import java.util.function.Function
import kotlin.math.abs
import kotlin.math.round


class Polynomial private constructor(_coefficients: List<Double>) : Function<Double, Double> {
    val coefficients = _coefficients

    override fun apply(x: Double) = coefficients.fold(0.0) { sum, v -> sum * x + v }
    fun degree() = coefficients.size - 1

    operator fun plus(other: Polynomial): Polynomial {
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
        return fromDoubles(p)
    }

    operator fun minus(other: Polynomial): Polynomial {
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
        return fromDoubles(p)
    }

    operator fun times(other: Polynomial): Polynomial {
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
        return fromDoubles(result)
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
        return Pair(fromDoubles(q), fromDoubles(r))
    }

    operator fun div(den: Polynomial) = divideAndRemainder(den).first
    operator fun rem(den: Polynomial) = divideAndRemainder(den).second

    operator fun div(den: Double) = canonicalValue(coefficients.asSequence().map { it / den})

    operator fun unaryPlus() = this

    operator fun unaryMinus() = when (this) {
        ZERO -> this
        else -> canonicalValue(coefficients.asSequence().map { -it })
    }

    fun negate() = unaryMinus()

    fun derivative(): Polynomial {
        val n = this.degree()
        if (n == 0) return ZERO
        val d = mutableListOf<Double>()
        var i = 0
        while (i < n) {
            d.add((n - i) * coefficients[i])
            ++i
        }
        return fromDoubles(d)
    }


    fun root(initial_guess: Double = 1.0, epsilon: Double = 1e-6, max_iterations: Int = 1000): Double {
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


        fun createFrom(vararg coefficients: Double) = fromDoubles(coefficients.asList())

        fun createFrom(vararg coefficients: Int) = fromInts(coefficients.asList())

        fun fromInts(coefficients: List<Int>) = when (coefficients.size) {
            0 -> ZERO
            1 -> when (coefficients[0]) {
                0 -> ZERO
                1 -> ONE
                else -> canonicalValue(coefficients.asSequence().map { it.toDouble() })
            }

            else -> nonTrivialList(coefficients.map { it.toDouble() })
        }

        fun fromDoubles(coefficients: List<Double>) = when (coefficients.size) {
            0 -> ZERO
            1 -> when (coefficients[0]) {
                0.0 -> ZERO
                1.0 -> ONE
                else -> canonicalValue(coefficients.asSequence())
            }

            else -> nonTrivialList(coefficients)
        }

        private fun nonTrivialList(coefficients: List<Double>): Polynomial {
            val c = coefficients.asSequence().dropWhile { x -> x == 0.0 }
            return canonicalValue(c)
        }

        private val instanceCache = mutableMapOf<List<Double>, Polynomial>()

        private fun store(coefficients: List<Double>): Polynomial {
            val p = Polynomial(coefficients)
            instanceCache[coefficients] = p
            return p
        }

        private fun canonicalValue(coefficients: Sequence<Double>): Polynomial {
            val c = coefficients.map { if (abs(it) == 0.0) 0.0 else it }.toList()
            return instanceCache[c] ?: Polynomial(c)
        }

        val ZERO = store(listOf(0.0))
        val ONE = store(listOf(1.0))
        val IDENTITY = store(listOf(1.0, 0.0))
        val SQUARE = store(listOf(1.0, 0.0, 0.0))
        val CUBE = store(listOf(1.0, 0.0, 0.0, 0.0))
        init {
            instanceCache[listOf()] = ZERO
        }
    }

}



