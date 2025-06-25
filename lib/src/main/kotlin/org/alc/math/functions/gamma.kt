package org.alc.math.functions

import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Suppress("ClassName")
object gamma {
    // Lanczos approximation coefficients
    @Suppress("FloatingPointLiteralPrecision")
    private val lanczos = doubleArrayOf(
        676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278686905,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7
    )

    operator fun invoke(z: Double): Double {
        if (z < 0.5) {
            // Use reflection formula for z < 0.5
            return PI / (sin(PI * z) * gamma(1.0 - z))
        } else {
            @Suppress("FloatingPointLiteralPrecision")
            var x = 0.99999999999980993
            val g = 7.0
            val zMinus1 = z - 1.0

            for (i in lanczos.indices) {
                x += lanczos[i] / (zMinus1 + i + 1.0)
            }

            val t = zMinus1 + g + 0.5
            return sqrt(2 * PI) * t.pow(zMinus1 + 0.5) * exp(-t) * x
        }
    }

    operator fun invoke(z: Number) = invoke(z.toDouble())
}
