package org.alc.parser

import org.alc.math.rational.Rational
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class ParserTest {

    @Test
    fun parseObjective() {
        val p = RationalParser("min 2x + 3*y - 5z + 4*foo")
        val obj = p.parseObjective()
        assertSame(Objective.Min, obj.obj)
        assertEquals(
            mapOf("x" to Rational(2), "y" to Rational(3), "z" to Rational(-5), "foo" to Rational(4)),
            obj.variables
        )
    }

    @Test
    fun parseConstraint() {
        val p = RationalParser("2x - 3y <= 5")
        val constraint = p.parseConstraint()
        assertSame(CompOp.LessThanOrEqual, constraint.comp)
        assertEquals(constraint.value, Rational(5))
        assertEquals(mapOf("x" to Rational(2), "y" to Rational(-3)), constraint.variables)
    }
}
