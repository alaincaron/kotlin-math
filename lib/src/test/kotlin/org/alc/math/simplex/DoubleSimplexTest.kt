package org.alc.math.simplex

import org.alc.math.matrix.DoubleMatrix
import org.alc.parser.CompOp
import org.alc.parser.ConstraintFunction
import org.alc.parser.Objective
import org.alc.parser.ObjectiveFunction
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DoubleSimplexTest {

    @Test
    fun test1() {
        val z = doubleArrayOf(4.0, 1.0, 4.0)
        val m = DoubleMatrix(3, 3)
        m[0, 0] = 2.0
        m[0, 1] = 1.0
        m[0, 2] = 1.0
        m[1, 0] = 1.0
        m[1, 1] = 2.0
        m[1, 2] = 3.0
        m[2, 0] = 2.0
        m[2, 1] = 2.0
        m[2, 2] = 1.0

        val c = doubleArrayOf(2.0, 4.0, 2.0)

        val soln = DoubleSimplexSolver.solve(z, m, c)
        assertEquals(
            Pair(listOf(0.4, 0.0, 1.2, 0.0, 0.0, 0.0, 6.4), 6.4),
            soln
        )
    }

    @Test
    fun testSolveString1() {
        val soln = DoubleSimplexSolver.solve(
            "max 4x + y + 4z",
            "2x + y + z <= 2",
            "x + 2y + 3z <= 4",
            "2x + 2y + z <= 2"
        )

        assertEquals(
            Pair(
                mapOf("x" to 0.4, "y" to 0.0, "z" to 1.2),
                6.4
            ),
            soln
        )

    }

    @Test
    fun testSolveParsed1() {
        val soln = DoubleSimplexSolver.solve(
            ObjectiveFunction(
                Objective.Max,
                mapOf("x" to 4.0, "y" to 1.0, "z" to 4.0)
            ),
            ConstraintFunction(
                CompOp.LessThanOrEqual,
                mapOf("x" to 2.0, "y" to 1.0, "z" to 1.0),
                2.0
            ),
            ConstraintFunction(
                CompOp.LessThanOrEqual,
                mapOf("x" to 1.0, "y" to 2.0, "z" to 3.0),
               4.0
            ),
            ConstraintFunction(
                CompOp.LessThanOrEqual,
                mapOf("x" to 2.0, "y" to 2.0, "z" to 1.0),
                2.0

            )
        )
        assertEquals(
            Pair(
                mapOf("x" to 0.4, "y" to 0.0, "z" to 1.2),
                6.4
            ),
            soln
        )
    }


    @Test
    fun test2() {
        val z = doubleArrayOf(4.0, 1.0, 4.0)
        val m = DoubleMatrix(3, 3)
        m[0, 0] = 1.0
        m[0, 1] = 2.0
        m[0, 2] = 3.0
        m[1, 0] = 2.0
        m[1, 1] = 1.0
        m[1, 2] = 1.0
        m[2, 0] = 2.0
        m[2, 1] = 2.0
        m[2, 2] = 1.0

        val c = doubleArrayOf(4.0, 2.0, 8.0)

        val soln = DoubleSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(listOf(0.4, 0.0, 1.2, 0.0, 0.0, 6.0, 6.4), 6.4),
            soln
        )
    }

    @Test
    fun test3() {
        val z = doubleArrayOf(2.5, 1.0)
        val m = DoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 1.0
        m[1, 0] = 1.0
        m[1, 1] = 1.0

        val c = doubleArrayOf(4.0, 6.0)

        val soln = DoubleSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(listOf(2.0, 0.0, 0.0, 4.0, 5.0), 5.0),
            soln
        )
    }

    @Test
    fun degenerate() {
        val z = doubleArrayOf(2.0, 1.0)
        val m = DoubleMatrix(3, 2)
        m[0, 0] = 4.0
        m[0, 1] = 3.0
        m[1, 0] = 4.0
        m[1, 1] = 1.0
        m[2, 0] = 4.0
        m[2, 1] = 2.0

        val c = doubleArrayOf(12.0, 8.0, 8.0)

        val soln = DoubleSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(listOf(2.0, 0.0, 4.0, 0.0, 0.0, 4.0), 4.0),
            soln
        )
    }

    @Test
    fun unbound() {
        val z = doubleArrayOf(2.0, 1.0)
        val m = DoubleMatrix(2, 2)
        m[0, 0] = 1.0
        m[0, 1] = -1.0
        m[1, 0] = 2.0
        m[1, 1] = -1.0

        val c = doubleArrayOf(10.0, 40.0)

        val soln = DoubleSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(listOf(30.0, 20.0, 0.0, 0.0, 80.0), Double.NaN),
            soln
        )
    }

    @Test
    fun multipleSolutions() {
        val z = doubleArrayOf(4.0, 14.0)
        val m = DoubleMatrix(2, 2)
        m[0, 0] = 2.0
        m[0, 1] = 7.0
        m[1, 0] = 7.0
        m[1, 1] = 2.0

        val c = doubleArrayOf(21.0, 21.0)

        val soln = DoubleSimplexSolver.solve(z, m, c)
        print("soln = $soln")

        assertEquals(
            Pair(listOf(0.0, 3.0, 0.0, 15.0, 42.0), 42.0),
            soln
        )
    }
}
