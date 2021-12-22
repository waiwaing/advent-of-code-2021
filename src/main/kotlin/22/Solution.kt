package `22`

import `20`.times
import kotlin.math.max
import kotlin.math.min

fun run_a(input: List<String>): String {
    val cube = List(101) { List(101) { MutableList(101) { false } } }

    input.forEach { line ->
        val axes = line.split(" x=", ",y=", ",z=", "..", "on", "off").filter { it != "" }.map(String::toInt)
        val (xs, yz, zs) = listOf(0, 2, 4).map { IntRange(max(axes[0 + it], -50), min(axes[1 + it], 50)) }

        (xs * yz * zs).forEach { (xy, z) -> cube[xy.first + 50][xy.second + 50][z + 50] = line.startsWith("on") }
    }

    return cube.sumOf { plane -> plane.sumOf { row -> row.count { it } } }.toString()
}

fun run_b(input: List<String>): String {
    val cubes = mutableSetOf<Cube>()

    input.forEach { line ->
        val cube = line.split(" x=", ",y=", ",z=", "..", "on", "off").filter { it != "" }.map(String::toInt)
            .let { Cube(it[0], it[1], it[2], it[3], it[4], it[5]) }

        if (line.startsWith("on")) {
            cubes.addCube(cube)
        } else {
            val overlaps = cubes.filter { it.overlaps(cube) }.toSet()
            cubes.removeAll(overlaps)
            cubes.addAll(overlaps.flatMap { it.divide(cube) }.filterNot { it.overlaps(cube) }.filter { it.isValid })
        }
    }

    return cubes.sumOf { it.size }.toString()
}

fun MutableSet<Cube>.addCube(cube: Cube) {
    val overlap = firstOrNull { it.overlaps(cube) } ?: return add(cube).let { }
    cube.divide(overlap).filterNot { it.overlaps(overlap) }.filter(Cube::isValid).forEach(::addCube)
}

data class Cube(val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int, val zMin: Int, val zMax: Int) {
    val size = (xMax - xMin + 1).toLong() * (yMax - yMin + 1) * (zMax - zMin + 1)
    val isValid = xMin <= xMax && yMin <= yMax && zMin <= zMax

    private fun overlapBounds(min1: Int, min2: Int, max1: Int, max2: Int) = listOf(max(min1, min2), min(max1, max2))
    private fun range(min1: Int, min2: Int, max2: Int, max1: Int) = overlapBounds(min1, min2, max1, max2)
        .let { listOf(Pair(min1, it[0] - 1), Pair(it[0], it[1]), Pair(it[1] + 1, max1)) }

    fun divide(b: Cube) =
        ((range(xMin, b.xMin, b.xMax, xMax) * range(yMin, b.yMin, b.yMax, yMax)) * range(zMin, b.zMin, b.zMax, zMax))
            .map { (w, z) -> Cube(w.first.first, w.first.second, w.second.first, w.second.second, z.first, z.second) }

    fun overlaps(b: Cube) =
        xMin <= b.xMax && xMax >= b.xMin && yMin <= b.yMax && yMax >= b.yMin && zMin <= b.zMax && zMax >= b.zMin
}
