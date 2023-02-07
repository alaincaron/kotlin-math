package org.alc.math.complex

import java.io.DataInput
import java.io.DataOutput
import java.util.regex.Pattern

internal const val signRegex = "[+-]?"
internal const val doubleRegex = "(?:(?:[0-9]*[.])?[0-9]+(?:e${signRegex}[0-9]+)?)"
internal val complexRegex = Pattern.compile(
    "($signRegex)(?:($doubleRegex?)(i?)|($doubleRegex)\\s*([+-])\\s*($doubleRegex?)i)$",
    Pattern.CASE_INSENSITIVE
)

/**
 * Creates a complex number from a string. A valid representation is e.g. "2.5+3.1i"
 * @return the created complex number
 */

fun String.toComplex(): Complex {
    if (this == "Infinity") return Complex.INFINITY
    if (this == "NaN") return Complex.NaN
    val m = complexRegex.matcher(this)
    if (!m.matches()) throw NumberFormatException("Invalid complex number: \"$this\"")
    val sign = if (m.group(1) == "-") -1.0 else 1.0
    if (m.group(2) != null) {
        if (m.group(2).isEmpty() && m.group(3).isEmpty())
            throw NumberFormatException("Invalid complex number: (empty string)")
        val v = if (m.group(2).isEmpty()) sign else m.group(2).toDouble() * sign
        return if (m.group(3).isEmpty()) Complex(v, 0.0) else Complex(0.0, v)
    }
    val re = m.group(4).toDouble() * sign
    val sign2 = if (m.group(5) == "-") -1.0 else 1.0

    val im = if (m.group(6).isEmpty()) sign2 else m.group(6).toDouble() * sign2
    return Complex(re, im)
}

fun DataInput.readComplex(): Complex {
    val re = this.readDouble()
    val im = this.readDouble()
    return Complex(re, im)
}

fun DataOutput.writeComplex(c: Complex)  {
    this.writeDouble(c.re)
    this.writeDouble(c.im)
}


