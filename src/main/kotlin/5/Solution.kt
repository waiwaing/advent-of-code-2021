package `5`

import kotlin.math.abs
import kotlin.math.max

fun run_a(input: List<String>): String {
    val grid = Array(1000) { IntArray(1000) }
    val regex = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")

    input
        .map {
            val (x1, y1, x2, y2) = regex.matchEntire(it)!!.destructured.toList().map(String::toInt)
            Line(x1, y1, x2, y2)
        }
        .filter { it.x1 == it.x2 || it.y1 == it.y2 }
        .forEach {
            val xs = generateSequence(it.x1) { n -> n + it.dx / it.distance }
            val ys = generateSequence(it.y1) { n -> n + it.dy / it.distance }

            xs.zip(ys)
                .takeWhile { (x, y) -> x != it.x2 || y != it.y2 }
                .plus(Pair(it.x2, it.y2))
                .forEach { (x, y) -> grid[x][y] += 1 }
        }

    return grid.sumOf { row -> row.count { it >= 2 } }.toString()
}

fun run_b(input: List<String>): String {
    val grid = Array(1000) { IntArray(1000) }
    val regex = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")

    input
        .map {
            val (x1, y1, x2, y2) = regex.matchEntire(it)!!.destructured.toList().map(String::toInt)
            Line(x1, y1, x2, y2)
        }
        .forEach {
            val xs = generateSequence(it.x1) { n -> n + it.dx / it.distance }
            val ys = generateSequence(it.y1) { n -> n + it.dy / it.distance }

            xs.zip(ys)
                .takeWhile { (x, y) -> x != it.x2 || y != it.y2 }
                .plus(Pair(it.x2, it.y2))
                .forEach { (x, y) -> grid[x][y] += 1 }
        }

    return grid.sumOf { row -> row.count { it >= 2 } }.toString()
}

data class Line(val x1: Int, val y1: Int, val x2: Int, val y2: Int) {
    val dx = x2 - x1
    val dy = y2 - y1
    val distance = max(abs(dx), abs(dy))
}