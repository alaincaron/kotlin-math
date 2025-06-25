package org.alc.math.complex

import org.alc.math.fix0
import org.alc.math.ring.DivisionRingElement
import java.util.*
import kotlin.math.atan

class Complex private constructor(val re: Double, val im: Double = 0.0) : DivisionRingElement<Complex> {

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
        val INFINITY = Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)

        val PI = Complex(Math.PI)

        operator fun invoke(re: Int, im: Int = 0) = invoke(re.toDouble(), im.toDouble())

        operator fun invoke(re: Double, im: Double = 0.0): Complex {
            if (re.isNaN() || im.isNaN()) return NaN
            if (re.isInfinite() || im.isInfinite()) return INFINITY

            return when (val c = Complex(fix0(re), fix0(im))) {
                ZERO -> ZERO
                ONE -> ONE
                NaN -> NaN
                INFINITY -> INFINITY
                I -> I
                else -> c
            }
        }
    }


    /** The argument of this complex number (angle of the polar coordinate representation) */
    val arg: Double
        get() {
            return when {
                isInfinite() -> Double.NaN
                isNaN() -> Double.NaN
                re > 0.0 -> atan(im / re)
                re < 0.0 && im >= 0.0 -> atan(im / re) + Math.PI
                re < 0.0 && im < 0.0 -> atan(im / re) - Math.PI
                re == 0.0 && im > 0.0 -> Math.PI / 2
                re == 0.0 && im < 0.0 -> - Math.PI / 2
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
    fun isInfinite() = re.isInfinite()

    /**
     * checks the "not a number" property (NaN represents an essential singularity)
     * @return true if this is NaN
     */
    fun isNaN() = re.isNaN()

    /**
     * checks to zero
     *  @return true if this is zero
     */
    fun isZero() = this == ZERO

    /**
     * Plus operator adding two complex numbers
     * @param other the summand
     * @return sum of this and z
     */
    override operator fun plus(other: Complex): Complex {
        return when {
            isNaN() || other.isNaN() -> NaN
            isInfinite() -> if (other.isInfinite()) NaN else INFINITY
            other.isInfinite() -> if (isInfinite()) NaN else INFINITY
            isZero() -> other
            other.isZero() -> this
            else -> invoke(re + other.re, im + other.im)
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
            isInfinite() -> if (x.isInfinite()) NaN else INFINITY
            x.isInfinite() -> if (isInfinite()) NaN else INFINITY
            isZero() -> x.R
            x == 0.0 -> this
            else -> invoke(re + x, im)
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
     * @param other the minuend
     * @return difference of this and x
     */
    override operator fun minus(other: Complex): Complex {
        return when {
            isNaN() || other.isNaN() -> NaN
            isInfinite() -> if (other.isInfinite()) NaN else INFINITY
            other.isInfinite() -> if (isInfinite()) NaN else INFINITY
            isZero() -> -other
            other.isZero() -> this
            else -> invoke(re - other.re, im - other.im)
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
            isInfinite() -> if (x.isInfinite()) NaN else INFINITY
            x.isInfinite() -> if (isInfinite()) NaN else INFINITY
            isZero() -> -x.R
            x == 0.0 -> this
            else -> invoke(re - x, im)
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
     * @param other the multiplicand
     * @return product of this and z
     */
    override operator fun times(other: Complex): Complex {
        return when {
            isNaN() || other.isNaN() -> NaN
            isInfinite() -> if (other.isZero()) NaN else INFINITY
            other.isInfinite() -> if (isZero()) NaN else INFINITY
            isZero() || other.isZero() -> ZERO
            other == ONE -> this
            this == ONE -> other
            else -> invoke(re * other.re - im * other.im, im * other.re + re * other.im)
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
            isInfinite() -> if (x == 0.0) NaN else INFINITY
            x.isInfinite() -> if (isZero()) NaN else INFINITY
            isZero() || x == 0.0 -> ZERO
            x == 1.0 -> this
            else -> invoke(re * x, im * x)
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
     * @param den the denominator
     * @return product of this and z
     */
    override operator fun div(den: Complex): Complex {
        return when {
            isNaN() || den.isNaN() -> NaN
            isInfinite() -> if (den.isInfinite()) NaN else INFINITY
            den.isInfinite() -> ZERO
            den.isZero() -> if (isZero()) NaN else INFINITY
            isZero() -> ZERO
            den == ONE -> this
            else -> {
                val d = den.re * den.re + den.im * den.im
                invoke((re * den.re + im * den.im) / d, (im * den.re - re * den.im) / d)
            }
        }
    }

    /**
     * Divide operator dividing a complex number and one of type Double
     * @param x the divisor
     * @return division of this and z
     */
    operator fun div(x: Double) = when {
        isNaN() || x.isNaN() -> NaN
        isInfinite() -> if (x.isInfinite()) NaN else INFINITY
        x.isInfinite() -> ZERO
        x == 0.0 -> if (isZero()) NaN else INFINITY
        isZero() -> ZERO
        x == 1.0 -> this
        else -> invoke(re / x, im / x)
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
    override operator fun unaryMinus(): Complex {
        return when {
            isNaN() -> NaN
            isInfinite() -> INFINITY
            isZero() -> ZERO
            else -> invoke(-re, -im)
        }
    }

    override operator fun unaryPlus() = this

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
            isInfinite() -> INFINITY
            isZero() -> ZERO
            else -> invoke(re, -im)
        }
    }

    fun invert() = when {
        isNaN() -> NaN
        isInfinite() -> ZERO
        isZero() -> INFINITY
        isZero() -> ZERO
        this == ONE -> this
        else -> {
            val d = re * re + im * im
            invoke(re / d, -im / d)
        }
    }

    fun sqrt() = sqrt(this)
    fun sin() = sin(this)
    fun pow(exponent: Complex) = pow(this, exponent)
    fun pow(exponent: Number) = pow(this, exponent.R)
    fun exp() = exp(this)

    /**
     * A string representation of a complex number (this) in the Form "2.5+3.1i" for example.
     * @param format This parameter affects the real and the imaginary part equally.
     * @param locale The locale determines e.g. whether a dot or a comma is output.
     */
    fun toStringBuilder(
        builder: StringBuilder? = null,
        format: String = "", locale: Locale = Locale.getDefault()
    ): StringBuilder {
        val b = builder ?: StringBuilder()
        return when (this) {
            NaN -> b.append("NaN")
            INFINITY -> b.append("Infinity")
            else -> {
                val reFormatted = if (format.isEmpty()) re.toString() else String.format(locale, format, re)
                val imFormatted = when (im) {
                    1.0 -> "i"
                    -1.0 -> "-i"
                    else -> "${if (format.isEmpty()) im.toString() else String.format(locale, format, im)}i"
                }
                b.append(
                    when {
                        re == 0.0 -> if (im == 0.0) "0.0" else imFormatted
                        im > 0.0 -> "$reFormatted+$imFormatted"
                        im < 0.0 -> "$reFormatted$imFormatted"
                        else -> reFormatted
                    }
                )
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
        return Objects.hash(re, im)
    }

    override fun toString() = toStringBuilder().toString()
    fun toString(format: String, locale: Locale = Locale.getDefault()) =
        toStringBuilder(null, format, locale).toString()

}

/**
 * Makes a number "imaginary".
 * @return this * I
 */
val Number.I: Complex
    get() = Complex(0.0, toDouble())

/**
 * Creates a complex number with this as real part and no imaginary part
 * @return this as complex number
 */
val Number.R: Complex
    get() = Complex(toDouble(), 0.0)

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
operator fun Number.div(z: Complex) = z.invert() * this





