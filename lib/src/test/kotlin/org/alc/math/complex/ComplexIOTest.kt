package org.alc.math.complex

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.*
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertSame

class ComplexIOTest {
    @Test
    fun testToComplex() {
        assertEquals(Complex.INFINITY, "Infinity".toComplex())
        assertEquals(Complex.NaN, "NaN".toComplex())
        assertEquals(Complex.I, "I".toComplex())
        assertEquals(-Complex.I, "-i".toComplex())
        assertEquals(Complex(2, 1), "2+i".toComplex())
        assertEquals(Complex(2, -1), "2-I".toComplex())
        assertEquals(4.R, "4".toComplex())
        assertEquals((Complex(-4, 0)), "-4".toComplex())
        assertEquals(2000.R, "2E3".toComplex())
        assertEquals(2000.I, "2e3i".toComplex())
        assertEquals(Complex(-2000), "-2e3".toComplex())
        assertEquals(Complex(0.0, -2000.0), "-2e3i".toComplex())
        assertEquals(Complex(2000.0, 2000.0), "2E3 + 2e3i".toComplex())
        assertEquals(Complex(-2000.0, -2000.0), "-2e3 - 2e3i".toComplex())
        assertThrows<NumberFormatException> { "".toComplex() }
        assertThrows<NumberFormatException> { "foobar".toComplex() }
        assertThrows<NumberFormatException> { "3+4j".toComplex() }
        assertThrows<NumberFormatException> { "+-3".toComplex() }
        assertThrows<NumberFormatException> { "2+-3i".toComplex() }
        assertThrows<NumberFormatException> { "2+3i-3-5i".toComplex() }
    }

    private fun serializeAndDeserialize(c: Complex): Complex {
        val arrayStream = ByteArrayOutputStream()
        val outStream = DataOutputStream(arrayStream)
        outStream.writeComplex(c)
        val inputStream = DataInputStream(ByteArrayInputStream(arrayStream.toByteArray()))
        return inputStream.readComplex()
    }

    @Test
    fun readWrite() {
        assertSame(Complex.INFINITY, serializeAndDeserialize(Complex.INFINITY))
        assertSame(Complex.NaN, serializeAndDeserialize(Complex.NaN))
        assertSame(Complex.ZERO, serializeAndDeserialize(Complex.ZERO))
        assertSame(Complex.I, serializeAndDeserialize(Complex.I))
        assertSame(Complex.ONE, serializeAndDeserialize(Complex.ONE))

        assertEquals(4.R, serializeAndDeserialize(4.R))
        assertEquals(4.R + 2.I, serializeAndDeserialize(4.R + 2.I))
        assertEquals(4.I, serializeAndDeserialize(4.I))
    }

    @Test
    fun scannerHasNextComplex() {
        assertTrue(Scanner("Infinity").hasNextComplex())
        assertTrue(Scanner("NaN").hasNextComplex())
        assertTrue(Scanner("0").hasNextComplex())
        assertTrue(Scanner("1+2i").hasNextComplex())
        assertFalse(Scanner("foobar").hasNextComplex())
    }

    @Test
    fun scannerNextComplex() {
        assertSame(Complex.INFINITY, Scanner("Infinity").nextComplex())
        assertSame(Complex.NaN, Scanner("NaN").nextComplex())
        assertEquals(Complex(1, 2), Scanner("1+2i").nextComplex())
        assertSame(Complex.ZERO, Scanner("0").nextComplex())
        assertThrows<InputMismatchException> { Scanner("foobar").nextComplex() }
    }
}
