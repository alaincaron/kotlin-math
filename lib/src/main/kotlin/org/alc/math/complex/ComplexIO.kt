package org.alc.math.complex

import java.io.DataInput
import java.io.DataOutput
import java.util.*
import java.util.regex.Pattern

internal const val signRegex = "[+-]?"
internal const val doubleRegex = "(?:(?:[0-9]*[.])?[0-9]+(?:[eE]${signRegex}[0-9]+)?)"
internal val complexRegex = Pattern.compile(
    "(Infinity)|(NaN)|($signRegex)(?:($doubleRegex?)([iI]?)|($doubleRegex)\\s*([+-])\\s*($doubleRegex?)[iI])$"
)

/**
 * Creates a complex number from a string. A valid representation is e.g. "2.5+3.1i"
 * @return the created complex number
 */

fun String.toComplex(): Complex {
    val m = complexRegex.matcher(this)
    if (!m.matches()) throw NumberFormatException("Invalid complex number: \"$this\"")
    if (m.group(1) != null) return Complex.INFINITY
    if (m.group(2) != null) return Complex.NaN
    val sign = if (m.group(3) == "-") -1.0 else 1.0
    if (m.group(4) != null) {
        if (m.group(4).isEmpty() && m.group(5).isEmpty())
            throw NumberFormatException("Invalid complex number: (empty string)")
        val v = if (m.group(4).isEmpty()) sign else m.group(4).toDouble() * sign
        return if (m.group(5).isEmpty()) Complex(v, 0.0) else Complex(0.0, v)
    }
    val re = m.group(6).toDouble() * sign
    val sign2 = if (m.group(7) == "-") -1.0 else 1.0

    val im = if (m.group(8).isEmpty()) sign2 else m.group(8).toDouble() * sign2
    return Complex(re, im)
}

fun DataInput.readComplex(): Complex {
    val re = this.readDouble()
    val im = this.readDouble()
    return Complex(re, im)
}

fun DataOutput.writeComplex(c: Complex) {
    this.writeDouble(c.re)
    this.writeDouble(c.im)
}

fun Scanner.hasNextComplex() = hasNext(complexRegex)
fun Scanner.nextComplex() = next(complexRegex).toComplex()


