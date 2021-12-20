package `20`

import `16`.toInt

operator fun <S, T> Iterable<S>.times(other: Iterable<T>) = this.flatMap { a -> other.map { b -> a to b } }

fun run_a(input: List<String>): String {
    val grid = Grid(input.drop(2)).runEnhancement(input[0].map { it == '#' }, 2)
    println(grid.litPixelCount())
    return ""
}

fun run_b(input: List<String>): String {
    val grid = Grid(input.drop(2)).runEnhancement(input[0].map { it == '#' }, 50)
    println(grid.litPixelCount())
    return ""
}

fun Grid.runEnhancement(algorithm: List<Boolean>, count: Int) = (1..count).fold(this) { grid, i ->
    val newGrid = Grid(listOf(), i % 2 != 0)

    ((grid.minX - 1..grid.maxX + 1) * (grid.minY - 1..grid.maxY + 1)).forEach { (x, y) ->
        val loc = Coordinates(x, y)
        newGrid.put(loc, algorithm[grid.threeByThreeValues(loc).toInt()])
    }

    newGrid
}

data class Coordinates(val x: Int, val y: Int) {
    fun centeredGridLocations() = ((-1..1) * (-1..1)).map { (dx, dy) -> Coordinates(dx + x, dy + y) }
}

class Grid(input: List<String> = listOf(), defaultVal: Boolean = false) {
    private val grid: MutableMap<Coordinates, Boolean> = input
        .flatMapIndexed { x, line -> line.trim().mapIndexed { y, value -> Coordinates(x, y) to (value == '#') } }
        .toMap().toMutableMap().withDefault { defaultVal }

    val minX by lazy { grid.keys.minOf { it.x } }
    val maxX by lazy { grid.keys.maxOf { it.x } }
    val minY by lazy { grid.keys.minOf { it.y } }
    val maxY by lazy { grid.keys.maxOf { it.y } }

    fun put(location: Coordinates, value: Boolean) = grid.put(location, value)

    fun threeByThreeValues(loc: Coordinates) = loc.centeredGridLocations().map { grid.getValue(it) }

    fun litPixelCount() = grid.count { it.value }
}
