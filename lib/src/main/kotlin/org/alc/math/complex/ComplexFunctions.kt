
package org.alc.math.complex

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sin

/**
 * Exponential function
 */
fun exp(z: Complex) = when (z) {
    Complex.NaN, Complex.INFINITY -> Complex.NaN
    else -> {
        val r: Double = kotlin.math.exp(z.re)
        Complex(r * cos(z.im), r * sin(z.im))
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
    Complex.ZERO, Complex.INFINITY, Complex.NaN -> Complex.NaN
    else -> Complex(kotlin.math.ln(z.mod), kotlin.math.atan2(z.im, z.re))
}

/**
 * Logarithmic function
 * @param z input
 * @return ln(z)
 */
fun ln(z: Number): Complex {
    val x = z.toDouble()
    return when {
        x == 0.0 || x.isNaN() || x.isInfinite() -> Complex.NaN
        x > 0 -> kotlin.math.ln(x).R
        else -> Complex(kotlin.math.ln(-x), Math.PI)
    }
}

/**
 * Sinus function
 */
fun sin(z: Complex) =
    when (z) {
        Complex.NaN, Complex.INFINITY -> Complex.NaN
        else -> Complex(
            sin(z.re) * kotlin.math.cosh(z.im),
            cos(z.re) * kotlin.math.sinh(z.im)
        )
    }

/**
 * Sinus function
 * @param z input
 * @return sin(z)
 */
fun sin(z: Number) = sin(z.toDouble()).R

/**
 * Cosine function
 */
fun cos(z: Complex) = when (z) {
    Complex.NaN, Complex.INFINITY -> Complex.NaN
    else ->
        Complex(
            cos(z.re) * kotlin.math.cosh(z.im),
            -sin(z.re) * kotlin.math.sinh(z.im)
        )
}

/**
 * Cosine function
 * @param z input
 * @return cos(z)
 */
fun cos(z: Number) = cos(z.toDouble()).R


/**
 * Main branch of the Square Root function
 */
fun sqrt(z: Complex) = when (z) {
    Complex.ZERO, Complex.INFINITY, Complex.NaN, Complex.ONE ->  z
    else -> {
        val r = z.mod
        val x = kotlin.math.sqrt((z.re + r) / 2.0)
        val y = sign(z.im) * kotlin.math.sqrt((r -z.re) / 2.0)
        Complex(x,y)
    }
}

/**
 * Square Root function
 * @param z input
 * @return sqrt(z)
 */
fun sqrt(z: Number): Complex {
    val r = z.toDouble()
    return when {
        r >= 0 -> kotlin.math.sqrt(r).R
        else -> kotlin.math.sqrt(-r).I
    }
}

/**
 * Calculates the complex power. 
 * value is returned here.
 * @param base
 * @param exponent
 * @return the power base ^ exponent
 */
fun pow(base: Complex, exponent: Complex) = when {
    base.isInfinite() -> infinitePow(exponent.re.sign, exponent.im.sign)
    exponent.im == 0.0 -> pow(base, exponent.re)
    else -> pow(base.mod, exponent) * exp(base.arg.I * exponent)
}

fun pow(base: Complex, exponent: Number): Complex {
    when (exponent) {
        is Int -> return pow(base, exponent)
        is Short -> return pow(base, exponent.toInt())
        is Byte -> return pow(base, exponent.toInt())
        is Double -> return pow(base, exponent)
        is Float -> return pow(base, exponent.toDouble())
    }
    return pow(base, exponent.R)
}

private fun pow(base: Complex, exponent: Double): Complex {
    if (base.isInfinite()) return infinitePow(exponent.sign)
    if (exponent.compareTo(exponent.toInt()) == 0) return pow(base, exponent.toInt())
    when {
        base.isZero() -> return Complex.ZERO
        base == Complex.ONE -> return Complex.ONE
        exponent.isNaN() -> return Complex.NaN
    }
    if (base.im == 0.0) return base.re.pow(exponent).R
    val r = base.mod.pow(exponent)
    val theta = base.arg * exponent
    return Complex(r * cos(theta), r * sin(theta))
//    val logBase = ln(base)
//    val scaledLog = Complex(logBase.re * exponent, logBase.im * exponent)
//    return exp(scaledLog)
}

private fun infinitePow(reSign: Double, imgSign: Double = 0.0) = when {
    reSign.isNaN() -> Complex.NaN
    reSign > 0 -> Complex.INFINITY
    reSign < 0 -> Complex.ZERO
    imgSign != 0.0 -> Complex.NaN
    else -> Complex.ONE
}
private fun pow(base: Complex, exponent: Int): Complex {
    when {
        exponent == 0 -> return Complex.ONE
        exponent == 1 -> return base
        base.isNaN() -> return Complex.NaN
        base.isZero() -> return if (exponent > 0) Complex.ZERO else Complex.INFINITY
        base.isInfinite() -> return infinitePow(sign(exponent.toDouble()))
    }
    //
    var result = Complex.ONE
    var b = base
    var e = if (exponent >= 0) exponent else -exponent

    while (e > 0) {
        if (e % 2 == 1) result *= b
        b *= b
        e /=2
    }
    return if (exponent >= 0) result else result.invert()
}
    
/**
 * The power function
 * @param base
 * @param exponent
 * @return base ^ exponent
 */
fun pow(base: Number, exponent: Complex): Complex {
    val d = base.toDouble()
    return when {
        exponent.isInfinite() -> Complex.NaN
        exponent.isNaN() -> Complex.NaN
        exponent.isZero() -> Complex.ONE
        d < 0.0 -> Complex.NaN
        d.isInfinite() -> Complex.NaN
        d.isNaN() -> Complex.NaN
        d == 0.0 -> Complex.ZERO
        else -> exp(kotlin.math.ln(d) * exponent)
    }
}


@Suppress("ClassName", "FloatingPointLiteralPrecision")
object gamma {
    private val lanczosCoefficients = listOf(
        676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278686905,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7
    ).map { Complex(it, 0.0) }

    private val sqrtTwoPi = Complex(2 * Math.PI, 0.0).sqrt()

    operator fun invoke(z: Complex): Complex {
        return if (z.re < 0.5) {
            // Reflection formula: Γ(z) = π / (sin(πz) * Γ(1 - z))
            val piZ = Complex(Math.PI) * z
            val sinPiZ = piZ.sin()
            val oneMinusZ = Complex(1.0, 0.0) - z
            Complex.PI / (sinPiZ * gamma(oneMinusZ))
        } else {
            var x = Complex(0.99999999999980993, 0.0)
            val zMinus1 = z - Complex(1.0, 0.0)

            for (i in lanczosCoefficients.indices) {
                val num = lanczosCoefficients[i]
                val den = zMinus1 + Complex((i + 1).toDouble(), 0.0)
                x += num / den
            }

            val t = zMinus1 + Complex( 7.5, 0.0)
            sqrtTwoPi * (t.pow(zMinus1 + Complex(0.5, 0.0))) * (-t).exp().unaryMinus() * x
        }
    }
}
