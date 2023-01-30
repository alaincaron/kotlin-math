package org.alc.math.complex

import org.alc.math.ring.DivisionRingElement
import org.alc.math.ring.RemainderRingElement
import java.util.*
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan

class Complex private constructor(val re: Double, val im: Double = 0.0): DivisionRingElement<Complex> {

    companion object {
        /** The imaginary unit i as constant */
        val I = Complex(0.0, 1.0)

        /** Number 0 as complex constant */
        val ZERO = Complex(0.0, 0.0)

        /** The real unit 1 as constant */
        val ONE = Complex(1.0, 0.0)

        /** "Not a number" represents an essential singularity */
        val NaN = Complex(Double.NaN, Double.NaN)

        /** Infinity represents the North Pole of the complex sphere. */
        val INF = Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

        const val DEFAULT_ZERO_SNAP_PRECISION = 1E-13

         fun create(z: Complex) = z
         fun create(str: String) = str.toComplex()
         fun create(re: Int, im: Int = 0) = create(re.toDouble(), im.toDouble())

        fun create(re: Double, im: Double) = when (val c = Complex(re, im)) {
            ZERO -> ZERO
            ONE -> ONE
            NaN -> NaN
            INF -> INF
            else -> c
        }
    }


    /** The argument of this complex number (angle of the polar coordinate representation) */
    val arg: Double
        get() {
            return when {
                isInfinite() -> Double.NaN
                isNaN() -> Double.NaN
                re > 0.0 -> atan(im / re)
                re < 0.0 && im >= 0.0 -> atan(im / re) + PI
                re < 0.0 && im < 0.0 -> atan(im / re) - PI
                re == 0.0 && im > 0.0 -> PI / 2
                re == 0.0 && im < 0.0 -> -PI / 2
                else -> 0.0
            }
        }

    /** The modulus (absolute value) of this complex number (radius of the polar coordinate representation)  */
    val mod: Double
        get() {
            return kotlin.math.sqrt(re * re + im * im)
        }

    /**
     *  checks infinity property (remark that in case of complex numbers there is only one unsigned infinity)
     *  @return true if this is infinite
     */
    fun isInfinite() = this === INF

    /**
     * checks the "not a number" property (NaN represents an essential singularity)
     * @return true if this is NaN
     */
    fun isNaN() = this === NaN

    /**
     * checks to zero
     *  @return true if this is zero
     */
    fun isZero() = this == ZERO

    /**
     * Plus operator adding two complex numbers
     * @param z the summand
     * @return sum of this and z
     */
    override operator fun plus(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isInfinite()) NaN else INF
            z.isInfinite() -> if (isInfinite()) NaN else INF
            isZero() -> z
            z.isZero() -> this
            else -> create(re + z.re, im + z.im)
        }
    }

    /**
     * Plus operator adding a complex number and a number of type Double
     * @param x the summand
     * @return sum of this and x
     */
    operator fun plus(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x.isInfinite()) NaN else INF
            x.isInfinite() -> if (isInfinite()) NaN else INF
            isZero() -> x.R
            x == 0.0 -> this
            else -> create(re + x, im)
        }
    }

    /**
     * Plus operator adding a complex number and one of type Number except Double
     * @param x the summand
     * @return sum of this and x
     */
    operator fun plus(x: Number) = plus(x.toDouble())

    /**
     * Minus operator subtracting two complex numbers
     * @param z the minuend
     * @return difference of this and x
     */
    override operator fun minus(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isInfinite()) NaN else INF
            z.isInfinite() -> if (isInfinite()) NaN else INF
            isZero() -> -z
            z.isZero() -> this
            else -> create(re - z.re, im - z.im)
        }
    }

    /**
     * Minus operator subtracting a complex number and one of type Double
     * @param x the minuend
     * @return difference of this and x
     */
    operator fun minus(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x.isInfinite()) NaN else INF
            x.isInfinite() -> if (isInfinite()) NaN else INF
            isZero() -> -x.R
            x == 0.0 -> this
            else -> create(re - x, im)
        }
    }

    /**
     * Minus operator subtracting a complex number and one of type Number except Double
     * @param x the minuend
     * @return difference of this and x
     */
    operator fun minus(x: Number) = minus(x.toDouble())

    /**
     * Times operator multiplying two complex numbers
     * @param z the multiplicand
     * @return product of this and z
     */
    override operator fun times(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isZero()) NaN else INF
            z.isInfinite() -> if (isZero()) NaN else INF
            isZero() || z.isZero() -> ZERO
            z == ONE -> this
            this == ONE -> z
            else -> create(re * z.re - im * z.im, im * z.re + re * z.im)
        }
    }

    /**
     * Times operator multiplying a complex number and one of type Double
     * @param x the multiplicand
     * @return product of this and x
     */
    operator fun times(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x == 0.0) NaN else INF
            x.isInfinite() -> if (isZero()) NaN else INF
            isZero() || x == 0.0 -> ZERO
            x == 1.0 -> this
            else -> create(re * x, im * x)
        }
    }

    /**
     * Times operator multiplying a complex number and one of type Number except Double
     * @param x the multiplicand
     * @return the product of this and x
     */
    operator fun times(x: Number) = times(x.toDouble())

    /**
     * Divide operator dividing two complex numbers
     * @param z the denominator
     * @return product of this and z
     */
    override operator fun div(z: Complex): Complex {
        return when {
            isNaN() || z.isNaN() -> NaN
            isInfinite() -> if (z.isInfinite()) NaN else INF
            z.isInfinite() -> ZERO
            z.isZero() -> if (isZero()) NaN else INF
            isZero() -> ZERO
            z == ONE -> this
            else -> {
                val d = z.re * z.re + z.im * z.im
                create((re * z.re + im * z.im) / d, (im * z.re - re * z.im) / d)
            }
        }
    }

    /**
     * Divide operator dividing a complex number and one of type Double
     * @param x the divisor
     * @return division of this and z
     */
    operator fun div(x: Double): Complex {
        return when {
            isNaN() || x.isNaN() -> NaN
            isInfinite() -> if (x.isInfinite()) NaN else INF
            x.isInfinite() -> ZERO
            x == 0.0 -> if (isZero()) NaN else INF
            isZero() -> ZERO
            x == 1.0 -> this
            else -> create(re / x, im / x)
        }
    }

    /**
     * Divide operator dividing a complex number and one of type Number except Double
     * @param x the divisor
     * @return division of this and z
     */
    operator fun div(x: Number) = div(x.toDouble())

    /**
     * Negates a complex number
     * @return negation of this
     */
    operator fun unaryMinus(): Complex {
        return when {
            isNaN() -> NaN
            isInfinite() -> INF
            isZero() -> ZERO
            else -> create(-re, -im)
        }
    }

    operator fun unaryPlus() = this

    /**
     * Calculates the complex conjugation
     * @return complex conjugation of this
     */
    operator fun not(): Complex = conj()

    /**
     * Calculates the complex conjugation
     * @return complex conjugation of this
     */
    fun conj(): Complex {
        return when {
            isNaN() -> NaN
            isInfinite() -> INF
            isZero() -> ZERO
            else -> create(re, -im)
        }
    }

    /**
     * Sets the real and/or the imaginary part to 0 if the value is lower than precision
     * @param precision
     * @return the "rounded" number
     */
    fun zeroSnap(precision: Double = DEFAULT_ZERO_SNAP_PRECISION): Complex {
        return create(
            if (abs(re) <= precision) 0.0 else re,
            if (abs(im) <= precision) 0.0 else im
        )
    }

    /**
     * A string representation of a complex number (this) in the Form "2.5+3.1i" for example.
     * @param format This parameter affects the real and the imaginary part equally.
     * @param locale The locale determines e.g. whether a dot or a comma is output.
     */
    fun asString(format: String = "", locale: Locale = Locale.getDefault()): String {
        return when (this) {
            NaN -> "NaN"
            INF -> "Infinity"
            else -> {
                val reFormatted = if (format.isEmpty()) re.toString() else String.format(locale, format, re)
                val imFormatted = when (im) {
                    1.0 -> "i"
                    -1.0 -> "-i"
                    else -> "${if (format.isEmpty()) im.toString() else String.format(locale, format, im)}i"
                }
                if (re == 0.0) {
                    if (im == 0.0) "0.0" else imFormatted
                } else {
                    when {
                        im > 0.0 -> "$reFormatted+$imFormatted"
                        im < 0.0 -> "$reFormatted$imFormatted"
                        else -> reFormatted
                    }
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other === null) return false
        if (other is Complex) {
            if (re != other.re) return false
            if (im != other.im) return false
            return true
        }
        return false
    }

    override fun hashCode(): Int {
        var result = re.hashCode()
        result = 31 * result + im.hashCode()
        return result
    }

    override fun toString() = asString()

}

/**
 * Makes a number "imaginary".
 * @return this * I
 */
val Number.I: Complex
    get() = Complex.create(0.0, toDouble())

/**
 * Creates a complex number with this as real part and no imaginary part
 * @return this as complex number
 */
val Number.R: Complex
    get() = Complex.create(toDouble(), 0.0)

/**
 * Plus operator adding a number of type Number and a complex one
 * @param z the summand
 * @return sum of this and z
 */
operator fun Number.plus(z: Complex) = z + this

/**
 * Minus operator subtracting a number of type Number and a complex one
 * @param z the minuend
 * @return difference of this and z
 */
operator fun Number.minus(z: Complex) = -z + this

/**
 * Times operator multiplying a number of type Number and a complex one
 * @param z the multiplicand
 * @return product of this and z
 */
operator fun Number.times(z: Complex) = z * this

/**
 * Division operator dividing a number of type Number and a complex one
 * @param z the divisor
 * @return division of this and z
 */
operator fun Number.div(z: Complex) = Complex.ONE / z * this

/**
 * Creates a complex number from a string. A valid representation is e.g. "2.5+3.1i"
 * @return the created complex number
 */
fun String.toComplex(): Complex {
    fun parseIm(arg: String): String {
        val im = arg.removeSuffix("i")
        return im.ifEmpty { "1.0" }
    }

    return when (this) {
        "Infinity" -> Complex.INF
        "NaN" -> Complex.NaN
        else -> {
            val parts = StringTokenizer(this, "+-", true)
                .toList().map { it.toString().replace('I', 'i') }
            when (parts.size) {
                0 -> throw NumberFormatException("empty String")
                1 -> if (parts[0].endsWith("i")) {
                    Complex.create(0.0, parseIm(parts[0]).toDouble())
                } else {
                    Complex.create(parts[0].toDouble(), 0.0)
                }

                2 -> if (parts[1].endsWith("i")) {
                    Complex.create(0.0, (parts[0] + parseIm(parts[1])).toDouble())
                } else {
                    Complex.create((parts[0] + parts[1]).toDouble(), 0.0)
                }

                3 -> Complex.create(parts[0].toDouble(), (parts[1] + parseIm(parts[2])).toDouble())
                4 -> Complex.create((parts[0] + parts[1]).toDouble(), (parts[2] + parseIm(parts[3])).toDouble())
                else -> throw NumberFormatException("For input string: \"$this\"")
            }
        }
    }
}



