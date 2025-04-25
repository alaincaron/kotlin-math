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
        assertSame(Token.Max, t.advance())
        assertEquals(Token.Constant(Rational(2.5)), t.advance())
        assertSame(Token.Times, t.advance())
        assertEquals(Token.Variable("foo"), t.advance())
        assertSame(Token.Minus, t.advance())
        assertEquals(Token.Constant(4 over 3), t.advance())
        assertSame(Token.Times, t.advance())
        assertEquals(Token.Variable("bar"), t.advance())
        assertSame(Token.LessThanOrEqual, t.advance())
        assertEquals(Token.Constant(Rational.TEN), t.advance())
        assertNull(t.advance())
    }
}
