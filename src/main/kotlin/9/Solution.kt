package `9`

import kotlin.math.abs

fun run_a(input: List<String>): String {
    val grid = Grid(input)

    return grid
        .filter { cell -> grid.neighbours(cell).all { neighbor -> cell.value < neighbor.value } }
        .sumOf { 1 + it.value }
        .toString()
}

fun run_b(input: List<String>): String {
    val grid = Grid(input)
    val queue = grid
        .filter { cell -> grid.neighbours(cell).all { neighbor -> cell.value < neighbor.value } }.toMutableList()
    val basins = queue.map { mutableListOf(it) }

    while (queue.size > 0) {
        val item = queue.removeFirst()
        val neighbors = grid.neighbours(item)
            .filter { neighbor -> basins.all { basin -> neighbor !in basin && neighbor.value != 9 } }

        basins.single { item in it }.addAll(neighbors)
        queue.addAll(neighbors)
    }

    return basins.map { it.size }.sortedDescending().take(3).reduce { x, y -> x * y }.toString()
}

class Cell(val x: Int, val y: Int, val value: Int)

class Grid(input: List<String>) : Iterable<Cell> {
    private val grid: List<Cell>
    private val lengthX: Int
    private val lengthY: Int

    init {
        grid = input.flatMapIndexed { x, line ->
            line.trim().mapIndexed { y, value ->
                Cell(x, y, value.digitToInt())
            }
        }

        lengthX = input.size
        lengthY = input[0].length
    }

    fun neighbours(cell: Cell) = grid.filter { abs(it.x - cell.x) + abs(it.y - cell.y) == 1 }

    override fun iterator(): Iterator<Cell> {
        return object : Iterator<Cell> {
            private var x = 0
            private var y = 0

            override fun hasNext() = y != lengthY

            override fun next(): Cell {
                val res = grid.single { it.x == x && it.y == y }

                x += 1
                if (x == lengthX) {
                    x = 0
                    y += 1
                }

                return res
            }
        }
    }


}