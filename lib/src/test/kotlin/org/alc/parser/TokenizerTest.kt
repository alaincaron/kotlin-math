package org.alc.parser

import org.alc.math.rational.Rational
import org.alc.math.rational.over
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TokenizerTest {

    @Test
    fun advance() {
        val input = "max 2.5 * foo - 4/3 * bar <= 10"
        val t = Tokenizer(input)
        assertSame(Objective.Max, t.advance())
        assertEquals(Operand.Constant(Rational(2.5)), t.advance())
        assertSame(Operator.Times, t.advance())
        assertEquals(Operand.Variable("foo"), t.advance())
        assertSame(Operator.Minus, t.advance())
        assertEquals(Operand.Constant(4 over 3), t.advance())
        assertSame(Operator.Times, t.advance())
        assertEquals(Operand.Variable("bar"), t.advance())
        assertSame(Comparator.LessThanOrEqual, t.advance())
        assertEquals(Operand.Constant(Rational.TEN), t.advance())
        assertNull(t.advance())
    }
}
