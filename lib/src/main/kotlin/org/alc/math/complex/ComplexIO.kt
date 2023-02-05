package org.alc.math.complex

import java.util.regex.Pattern

internal const val doubleRegex = "(?:[-+]?(?:[0-9]*[.])?[0-9]+(?:e[-+]?[0-9]+)?)"
internal val complexRegex = Pattern.compile(
    "^($doubleRegex)(i?)|($doubleRegex)\\s*([+-])\\s*($doubleRegex)i$",
    Pattern.CASE_INSENSITIVE
)

/**
 * Creates a complex number from a string. A valid representation is e.g. "2.5+3.1i"
 * @return the created complex number
 */

fun String.toComplex(): Complex {
    if (this == "Infinity") return Complex.INF
    if (this == "NaN") return Complex.NaN
    val m = complexRegex.matcher(this)
    if (!m.matches()) throw NumberFormatException("Invalid complex number: \"$this\"")
    if (m.group(1) != null) {
        val v = m.group(1).toDouble()
        return if (m.group(2).isEmpty()) Complex(v, 0.0) else Complex(0.0, v)
    }
    val re = m.group(3).toDouble()
    if (m.group(5).startsWith("-")) throw NumberFormatException("Invalid complex number: \"$this\"")
    val im = m.group(5).toDouble()
    return if (m.group(4) == "+")
        Complex(re, im)
    else
        Complex(re, -im)
}



