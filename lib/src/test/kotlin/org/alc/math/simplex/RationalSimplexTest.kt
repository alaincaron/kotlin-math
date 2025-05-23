package org.alc.math.simplex

import org.alc.math.matrix.RationalMatrix
import org.alc.math.rational.Rational
import org.alc.math.rational.over
import org.alc.parser.CompOp
import org.alc.parser.ConstraintFunction
import org.alc.parser.Objective
import org.alc.parser.ObjectiveFunction
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class RationalSimplexTest {

    @Test
    fun testSolve1() {
        val z = arrayOf(4 over 1, 1 over 1, 4 over 1)
        val m = RationalMatrix(3, 3)
        m[0, 0] = 2 over 1
        m[0, 1] = 1 over 1
        m[0, 2] = 1 over 1
        m[1, 0] = 1 over 1
        m[1, 1] = 2 over 1
        m[1, 2] = 3 over 1
        m[2, 0] = 2 over 1
        m[2, 1] = 2 over 1
        m[2, 2] = 1 over 1

        val c = arrayOf(2 over 1, 4 over 1, 2 over 1)

        val soln = RationalSimplexSolver.solve(z, m, c)
        assertEquals(
            Pair(
                listOf(2 over 5, 0 over 1, 6 over 5, 0 over 1, 0 over 1, 0 over 1, 32 over 5),
                32 over 5
            ),
            soln
        )
    }

    @Test
    fun testSolveString1() {
        val soln = RationalSimplexSolver.solve(
            "max 4x + y + 4z",
            "2x + y + z <= 2",
            "x + 2y + 3z <= 4",
            "2x + 2y + z <= 2"
        )

        assertEquals(
            Pair(
                mapOf("x" to (2 over 5), "y" to Rational.ZERO, "z" to (6 over 5)),
                32 over 5
            ),
            soln
        )

    }

    @Test
    fun testSolveParsed1() {
        val soln = RationalSimplexSolver.solve(
            ObjectiveFunction(
                Objective.Max,
                mapOf("x" to Rational(4), "y" to Rational.ONE, "z" to Rational(4))
            ),
            ConstraintFunction(
                CompOp.LessThanOrEqual,
                mapOf("x" to Rational(2), "y" to Rational.ONE, "z" to Rational.ONE),
                Rational(2)
            ),
            ConstraintFunction(
                CompOp.LessThanOrEqual,
                mapOf("x" to Rational.ONE, "y" to Rational.TWO, "z" to Rational(3)),
                Rational(4)
            ),
            ConstraintFunction(
                CompOp.LessThanOrEqual,
                mapOf("x" to Rational.TWO, "y" to Rational.TWO, "z" to Rational.ONE),
                Rational.TWO

            )
        )
        assertEquals(
            Pair(
                mapOf("x" to (2 over 5), "y" to Rational.ZERO, "z" to (6 over 5)),
                32 over 5
            ),
            soln
        )
    }

    @Test
    fun test2() {
        val z = arrayOf(4 over 1, 1 over 1, 4 over 1)
        val m = RationalMatrix(3, 3)
        m[0, 0] = 1 over 1
        m[0, 1] = 2 over 1
        m[0, 2] = 3 over 1
        m[1, 0] = 2 over 1
        m[1, 1] = 1 over 1
        m[1, 2] = 1 over 1
        m[2, 0] = 2 over 1
        m[2, 1] = 2 over 1
        m[2, 2] = 1 over 1

        val c = arrayOf(4 over 1, 2 over 1, 8 over 1)

        val soln = RationalSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(
                listOf(2 over 5, 0 over 1, 6 over 5, 0 over 1, 0 over 1, 6 over 1, 32 over 5),
                32 over 5
            ),
            soln
        )
    }

    @Test
    fun test3() {
        val z = arrayOf(5 over 2, 1 over 1)
        val m = RationalMatrix(2, 2)
        m[0, 0] = 2 over 1
        m[0, 1] = 1 over 1
        m[1, 0] = 1 over 1
        m[1, 1] = 1 over 1

        val c = arrayOf(4 over 1, 6 over 1)

        val soln = RationalSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(
                listOf(2 over 1, 0 over 1, 0 over 1, 4 over 1, 5 over 1),
                5 over 1
            ),
            soln
        )
    }

    @Test
    fun degenerate() {
        val z = arrayOf(2 over 1, 1 over 1)
        val m = RationalMatrix(3, 2)
        m[0, 0] = 4 over 1
        m[0, 1] = 3 over 1
        m[1, 0] = 4 over 1
        m[1, 1] = 1 over 1
        m[2, 0] = 4 over 1
        m[2, 1] = 2 over 1

        val c = arrayOf(12 over 1, 8 over 1, 8 over 1)

        val soln = RationalSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(listOf(2 over 1, 0 over 1, 4 over 1, 0 over 1, 0 over 1, 4 over 1), 4 over 1),
            soln
        )
    }

    @Test
    fun unbound() {
        val z = arrayOf(2 over 1, 1 over 1)
        val m = RationalMatrix(2, 2)
        m[0, 0] = 1 over 1
        m[0, 1] = -1 over 1
        m[1, 0] = 2 over 1
        m[1, 1] = -1 over 1

        val c = arrayOf(10 over 1, 40 over 1)

        val soln = RationalSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(listOf(30 over 1, 20 over 1, 0 over 1, 0 over 1, 80 over 1), soln.first)
        assertSame(Rational.NaN, soln.second)
    }

    @Test
    fun multipleSolutions() {
        val z = arrayOf(4 over 1, 14 over 1)
        val m = RationalMatrix(2, 2)
        m[0, 0] = 2 over 1
        m[0, 1] = 7 over 1
        m[1, 0] = 7 over 1
        m[1, 1] = 2 over 1

        val c = arrayOf(21 over 1, 21 over 1)

        val soln = RationalSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(listOf(0 over 1, 3 over 1, 0 over 1, 15 over 1, 42 over 1), 42 over 1),
            soln
        )

    }
}
