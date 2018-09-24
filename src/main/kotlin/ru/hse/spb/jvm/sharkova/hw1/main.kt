package ru.hse.spb.jvm.sharkova.hw1

import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.max

fun solve(n: Int, w: Int, h: Int, g: IntArray, p: IntArray, t: IntArray): Array<Pair<Int, Int>> {
    val answer = Array(n) { Pair(0, 0) }

    val maxT = t.max() ?: 0
    val s = Array(max(w, h) + maxT) { ArrayList<Int>() }
    for (i in 0 until n) {
        s[p[i] - t[i] + maxT].add(i)
    }

    for (list in s.filter { it.isNotEmpty() }) {
        val xs = ArrayList<Int>()
        val ys = ArrayList<Int>()

        for (i in list) {
            if (g[i] == 1) {
                xs.add(p[i])
            } else {
                ys.add(p[i])
            }
        }
        xs.sort()
        ys.sort()
        list.sortWith(Comparator { u, v ->
            if (u == null || v == null) {
                return@Comparator 0
            }

            if (g[u] != g[v]) {
                if (g[u] == 2) -1 else 1
            } else {
                if (g[u] == 2) p[v].compareTo(p[u]) else p[u].compareTo(p[v])
            }
        })

        for (j in 0 until xs.size) {
            answer[list[j]] = Pair(xs[j], h)
        }
        for (j in 0 until ys.size) {
            answer[list[j + xs.size]] = Pair(w, ys[ys.size - j - 1])
        }
    }

    return answer
}

fun main(args: Array<String>) {
    val reader = Scanner(System.`in`)

    val n = reader.nextInt()
    val w = reader.nextInt()
    val h = reader.nextInt()

    val g = IntArray(n)
    val p = IntArray(n)
    val t = IntArray(n)

    for (i in 0 until n) {
        g[i] = reader.nextInt()
        p[i] = reader.nextInt()
        t[i] = reader.nextInt()
    }

    val answer = solve(n, w, h, g, p, t)

    for (i in 0 until n) {
        println("${answer[i].first} ${answer[i].second}")
    }

}