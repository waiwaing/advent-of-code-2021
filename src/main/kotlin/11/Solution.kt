package `11`

import kotlin.math.abs

fun run_a(input: List<String>): String {
    val grid = Grid(input)

    return (1..100).sumOf {
        grid.forEach { cell -> cell.value += 1 }

        while (true) {
            val cell = grid.firstOrNull { cell -> cell.value > 9 } ?: break
            grid.allNeighbors(cell)
                .forEach {
                    if (it.value != 0) {
                        it.value += 1
                    }
                }
            cell.value = 0
        }

        grid.count { it.value == 0 }
    }.toString()
}

fun run_b(input: List<String>): String {
    val grid = Grid(input)
    var iterations = 0

    while (true) {
        if (grid.map { it.value }.toSet().size == 1) {
            return iterations.toString()
        }

        iterations += 1
        grid.forEach { cell -> cell.value += 1 }

        while (true) {
            val cell = grid.firstOrNull { cell -> cell.value > 9 } ?: break
            grid.allNeighbors(cell)
                .forEach {
                    if (it.value != 0) {
                        it.value += 1
                    }
                }
            cell.value = 0
        }
    }
}


class Cell(val x: Int, val y: Int, var value: Int)

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

    fun allNeighbors(cell: Cell) = grid
        .filter { abs(it.x - cell.x) <= 1 && abs(it.y - cell.y) <= 1 }
        .filter { it != cell }

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

    fun print() {
        grid.map { it.value }.chunked(lengthX).forEach { println(it.joinToString("")) }
        println("------")
    }
}