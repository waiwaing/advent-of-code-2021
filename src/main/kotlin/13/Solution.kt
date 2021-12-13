package `13`

fun run_a(input: List<String>): String {
    val gridSize = 1500
    val grid = Array(gridSize) { BooleanArray(gridSize) }

    input
        .filter { "," in it }
        .forEach {
            val (x, y) = it.split(",").map(String::toInt)
            grid[x][y] = true
        }

    val fold = input.first { "fold along" in it }.substringAfter("fold along ")

    val axis = fold[0]
    val axisIndex = fold.substring(2).toInt()

    (0 until gridSize).forEach { i ->
        (0 until gridSize).forEach { j ->
            val (x, y, xB, yB) = when (axis) {
                'x' -> listOf(j, i, 2 * axisIndex - j, i)
                else -> listOf(i, j, i, 2 * axisIndex - j)
            }
            grid[x][y] = j < axisIndex && (grid[x][y] || grid[xB][yB])
        }
    }

    return grid.sumOf { row -> row.count { it } }.toString()
}

fun run_b(input: List<String>): String {
    val gridSize = 1500
    val grid = Array(gridSize) { BooleanArray(gridSize) }

    input
        .filter { "," in it }
        .forEach {
            val (x, y) = it.split(",").map(String::toInt)
            grid[x][y] = true
        }

    input.filter { "fold along" in it }
        .forEach {
            val fold = it.substringAfter("fold along ")

            val axis = fold[0]
            val axisIndex = fold.substring(2).toInt()

            (0 until gridSize).forEach { i ->
                (0 until gridSize).forEach { j ->
                    val (x, y, xB, yB) = when (axis) {
                        'x' -> listOf(j, i, 2 * axisIndex - j, i)
                        else -> listOf(i, j, i, 2 * axisIndex - j)
                    }
                    grid[x][y] = j < axisIndex && (grid[x][y] || grid[xB][yB])
                }
            }
        }

    printGrid(grid)

    return ""
}

fun printGrid(grid: Array<BooleanArray>) {
    (0 until grid[0].size).map { y ->
        grid
            .map { row -> row[y] }
            .map { value -> if (value) '#' else ' ' }
            .joinToString("")
            .trimEnd()
            .let { if (it != "") println(it) }
    }
}