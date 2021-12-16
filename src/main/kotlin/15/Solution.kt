package `15`

import java.util.PriorityQueue

fun run_a(input: List<String>): String {
    val grid = Grid(input)

    grid.runDijkstra()
    return grid.bottomRight().cost.toString()
}

fun run_b(input: List<String>): String {
    val grid = Grid(input)
    grid.expandGrid()

    grid.runDijkstra()
    return grid.bottomRight().cost.toString()
}

fun <S, T> Iterable<S>.cartesianProduct(other: Iterable<T>) = this.flatMap { a -> other.map { b -> a to b } }

class Cell(val x: Int, val y: Int, val value: Int, var cost: Int)

class Grid(input: List<String>) {
    private val grid = mutableMapOf<Pair<Int, Int>, Cell>()
    private val priorityQueue = PriorityQueue<Cell> { o1, o2 -> o1.cost - o2.cost }
    private fun size() = Pair(grid.maxOf { it.key.first } + 1, grid.maxOf { it.key.second } + 1)

    init {
        input.flatMapIndexed { x, line ->
            line.trim().mapIndexed { y, value ->
                Cell(x, y, value.digitToInt(), Int.MAX_VALUE)
            }
        }.associateByTo(grid) { Pair(it.x, it.y) }
    }

    fun expandGrid() {
        val originalGrid = grid.toMap()
        val originalSize = size()

        (0..4).cartesianProduct((0..4)).forEach { (x, y) ->
            if (x == 0 && y == 0) return@forEach

            originalGrid.map { (_, it) ->
                val newValue = (it.value + x + y).toString().map(Char::digitToInt).sum()
                Cell(it.x + x * originalSize.first, it.y + y * originalSize.second, newValue, Int.MAX_VALUE)
            }
                .associateByTo(grid) { Pair(it.x, it.y) }
        }
    }

    fun runDijkstra() {
        setCost(grid[Pair(0, 0)]!!, 0)

        while (priorityQueue.isNotEmpty()) {
            val node = priorityQueue.poll()

            listOfNotNull(
                grid[Pair(node.x - 1, node.y)], grid[Pair(node.x + 1, node.y)],
                grid[Pair(node.x, node.y - 1)], grid[Pair(node.x, node.y + 1)]
            )
                .filter { node.cost + it.value < it.cost }
                .forEach { setCost(it, node.cost + it.value) }
        }
    }

    fun bottomRight(): Cell = grid[Pair(size().first - 1, size().second - 1)]!!

    private fun setCost(cell: Cell, cost: Int) {
        cell.cost = cost

        priorityQueue.remove(cell)
        priorityQueue.add(cell)
    }
}