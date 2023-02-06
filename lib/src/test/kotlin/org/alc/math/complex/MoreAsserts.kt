package org.alc.math.complex

import kotlin.test.assertEquals
import kotlin.test.assertTrue

fun assertQuasiEquals(
    expected: Double, actual: Double,
    tolerance: Double = 1e-13,
    message: String? = null
) =
    assertEquals(expected, actual, tolerance, message)

fun assertQuasiEquals(
    expected: Complex, actual: Complex,
    tolerance: Double = 1e-13,
    message: String? = null
) =
    assertTrue(
        (expected - actual).mod < tolerance,
        message ?: "Expected <$expected>, actual <$actual>"
    )
