package org.alc.parser

import org.alc.math.rational.Rational
import org.alc.math.rational.over
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TokenizerTest {

    @Test
    fun advance() {
        val input = "max 2.5 * foo - 4/3 * bar <= 10"
        val t = RationalTokenizer(input)
        assertSame(Objective.Max, t.advance())
        assertEquals(Operand.Constant(Rational(2.5)), t.advance())
        assertSame(BinOp.Times, t.advance())
        assertEquals(Operand.Variable("foo"), t.advance())
        assertSame(BinOp.Minus, t.advance())
        assertEquals(Operand.Constant(4 over 3), t.advance())
        assertSame(BinOp.Times, t.advance())
        assertEquals(Operand.Variable("bar"), t.advance())
        assertSame(CompOp.LessThanOrEqual, t.advance())
        assertEquals(Operand.Constant(Rational.TEN), t.advance())
        assertNull(t.advance())
    }
}
