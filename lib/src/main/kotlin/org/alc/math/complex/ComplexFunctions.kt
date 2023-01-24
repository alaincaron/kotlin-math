package org.alc.math.complex

import kotlin.math.withSign

/**
 * Exponential function
 */
fun exp(z: Complex) = when (z) {
    Complex.NaN, Complex.INF -> Complex.NaN
    else -> {
        val r: Double = kotlin.math.exp(z.re)
        Complex.create(r * kotlin.math.cos(z.im), r * kotlin.math.sin(z.im))
    }
}

/**
 * Exponential function
 * @param z input
 * @return exp(z)
 */
fun exp(z: Number) = exp(z.R)

/**
 * Main branch of the Logarithmic function
 */
fun ln(z: Complex) = when (z) {
    Complex.ZERO, Complex.INF, Complex.NaN -> Complex.NaN
    else -> Complex.create(kotlin.math.ln(z.mod), kotlin.math.atan2(z.im, z.re))
}

/**
 * Logarithmic function
 * @param z input
 * @return ln(z)
 */
fun ln(z: Number) = ln(z.R)

/**
 * Sinus function
 */
fun sin(z: Complex) =
    when (z) {
        Complex.NaN, Complex.INF -> Complex.NaN
        else -> Complex.create(
            kotlin.math.sin(z.re) * kotlin.math.cosh(z.im),
            kotlin.math.cos(z.re) * kotlin.math.sinh(z.im)
        )
    }

/**
 * Sinus function
 * @param z input
 * @return sin(z)
 */
fun sin(z: Number) = sin(z.R)

/**
 * Cosine function
 */
fun cos(z: Complex) = when (z) {
    Complex.NaN, Complex.INF -> Complex.NaN
    else ->
        Complex.create(
            kotlin.math.cos(z.re) * kotlin.math.cosh(z.im),
            -kotlin.math.sin(z.re) * kotlin.math.sinh(z.im)
        )
}

/**
 * Cosine function
 * @param z input
 * @return cos(z)
 */
fun cos(z: Number) = cos(z.R)


/**
 * Main branch of the Square Root function
 */
fun sqrt(z: Complex) = when (z) {
        Complex.ZERO, Complex.INF, Complex.NaN -> z
        else -> {
            val t: Double = kotlin.math.sqrt((kotlin.math.abs(z.re) + z.mod) / 2)
            if (z.re >= 0) {
                Complex.create(t, z.im / (2 * t))
            } else {
                Complex.create(kotlin.math.abs(z.im) / (2 * t), 1.0.withSign(z.im) * t)
            }
        }
    }

/**
 * Square Root function
 * @param z input
 * @return sqrt(z)
 */
fun sqrt(z: Number) = sqrt(z.R)

/**
 * Calculates the complex power. Please note, that similar to ln and sqrt the default
 * value is returned here.
 * @param z basis
 * @param w exponent
 * @return the power z^w
 */
fun pow(z: Complex, w: Complex) = pow(z.mod, w) * exp(z.arg.I * w)

/**
 * The power function
 * @param x base
 * @param w exponent
 * @return x^w
 */
fun pow(x: Number, w: Complex): Complex {
    val d = x.toDouble()
    return when {
        d < 0.0 -> Complex.NaN
        d.isInfinite() -> Complex.NaN
        d.isNaN() -> Complex.NaN
        w.isInfinite() -> Complex.NaN
        w.isNaN() -> Complex.NaN
        w.isZero() -> Complex.ONE
        d == 0.0 -> Complex.ZERO
        else -> exp(kotlin.math.ln(d) * w)
    }
}
