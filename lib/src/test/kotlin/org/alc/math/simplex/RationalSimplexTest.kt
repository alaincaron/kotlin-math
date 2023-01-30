package org.alc.math.simplex
import kotlin.test.*

import org.alc.math.matrix.RationalMatrix
import org.alc.math.rational.Rational
import org.alc.math.rational.over
import org.junit.jupiter.api.Test

class RationalSimplexTest {

    @Test
    fun test1() {
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
                listOf(2 over 5,0 over 1, 6 over 5, 0 over 1, 0 over 1, 6 over 1, 32 over 5),
                32 over 5
            ),
            soln
        )
    }

    @Test
    fun test3() {
        val z = arrayOf(5 over 2 , 1 over 1)
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

}
