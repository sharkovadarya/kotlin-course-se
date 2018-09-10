package ru.hse.spb.jvm.sharkova.hw1

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class SolverTest {
    //example tests are taken from the problem 849D description on codeforces.com

    @Test
    fun exampleTest1() {
        val n = 8
        val w = 10
        val h = 8
        val g = intArrayOf(1, 1, 1, 1, 2, 2, 2, 2)
        val p = intArrayOf(1, 4, 7, 8, 2, 5, 6, 6)
        val t = intArrayOf(10, 13, 1, 2, 0, 14, 0, 1)

        val answer = Solver.solve(n, w, h, g, p, t)
        assertEquals(answer[0], Pair(4, 8))
        assertEquals(answer[1], Pair(10, 5))
        assertEquals(answer[2], Pair(8, 8))
        assertEquals(answer[3], Pair(10, 6))
        assertEquals(answer[4], Pair(10, 2))
        assertEquals(answer[5], Pair(1, 8))
        assertEquals(answer[6], Pair(7, 8))
        assertEquals(answer[7], Pair(10, 6))
    }

    @Test
    fun exampleTest2() {
        val n = 3
        val w = 2
        val h = 3
        val g = intArrayOf(1, 2, 1)
        val p = intArrayOf(1, 1, 1)
        val t = intArrayOf(2, 1, 5)

        val answer = Solver.solve(n, w, h, g, p, t)
        assertEquals(answer[0], Pair(1, 3))
        assertEquals(answer[1], Pair(2, 1))
        assertEquals(answer[2], Pair(1, 3))
    }

    // the following test was taken from the codeforces tests set
    @Test
    fun codeforcesTest() {
        val n = 20
        val w = 15
        val h = 15
        val g = intArrayOf(2, 1, 2, 1, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 2, 1, 1, 1, 1, 2)
        val p = intArrayOf(7, 2, 1, 9, 4, 3, 14, 6, 10, 5, 13, 8, 13, 14, 10, 5, 11, 12, 1, 2)
        val t = IntArray(n) { 100000 }

        val answer = Solver.solve(n, w, h, g, p, t)
        val correctAnswer = arrayOf(Pair(15, 7), Pair(15, 2), Pair(1, 15), Pair(9, 15), Pair(15, 4), Pair(15, 3),
                Pair(14, 15), Pair(6, 15), Pair(15, 10), Pair(5, 15), Pair(13, 15), Pair(8, 15), Pair(15, 13),
                Pair(15, 14), Pair(10, 15), Pair(15, 5), Pair(11, 15), Pair(12, 15), Pair(15, 1), Pair(2, 15))
        assertTrue(Arrays.equals(answer, correctAnswer))
    }

    @Test
    fun testNoWaitingTime() {
        val n = 4
        val w = 5
        val h = 5
        val g = intArrayOf(1, 2, 1, 2)
        val p = intArrayOf(1, 1, 2, 2)
        val t = intArrayOf(0, 0, 0, 0)

        val answer = Solver.solve(n, w, h, g, p, t)
        val correctAnswer = arrayOf(Pair(5, 1), Pair(1, 5), Pair(5, 2), Pair(2, 5))
        assertTrue(Arrays.equals(answer, correctAnswer))
    }

    @Test
    fun testManyCollisions() {
        val n = 6
        val w = 5
        val h = 5
        val g = intArrayOf(1, 1, 1, 2, 2, 2)
        val p = intArrayOf(1, 2, 3, 1, 2, 3)
        val t = intArrayOf(0, 1, 2, 0, 1, 2)

        val answer = Solver.solve(n, w, h, g, p, t)
        val correctAnswer = arrayOf(Pair(5, 3), Pair(5, 2), Pair(5, 1), Pair(3, 5), Pair(2, 5), Pair(1, 5))
        assertTrue(Arrays.equals(answer, correctAnswer))
    }

    @Test
    fun testNoCollisions() {
        val n = 2
        val w = 3
        val h = 6
        val g = intArrayOf(1, 2)
        val p = intArrayOf(2, 5)
        val t = intArrayOf(5, 0)

        val answer = Solver.solve(n, w, h, g, p, t)
        val correctAnswer = arrayOf(Pair(2, 6), Pair(3, 5))
        assertTrue(Arrays.equals(answer, correctAnswer))
    }

    @Test
    fun testManyDancers() {
        val n = 10
        val w = 6
        val h = 6
        val g = intArrayOf(1, 2, 1, 2, 1, 2, 1, 2, 1, 2)
        val p = intArrayOf(1, 1, 2, 2, 3, 3, 4, 4, 5, 5)
        val t = IntArray(n) { 100000 }

        val answer = Solver.solve(n, w, h, g, p, t)
        val correctAnswer = arrayOf(Pair(6, 1), Pair(1, 6), Pair(6, 2), Pair(2, 6), Pair(6, 3), Pair(3, 6),
                Pair(6, 4), Pair(4, 6), Pair(6, 5), Pair(5, 6))
        assertTrue(Arrays.equals(answer, correctAnswer))
    }
}
