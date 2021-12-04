package `4`

fun run_a(input: List<String>): String {
    val numbers = input.first().split(",").map(String::toInt)
    val boards = input.drop(1).filter { it.trim() != "" }.windowed(5, 5).map { BingoBoard(it) }

    for (x in numbers) {
        boards.forEach { it.remove(x) }
        val winners = boards.filter { it.hasWon() }
        if (winners.any()) {
            return (winners[0].remainingNumSum() * x).toString()
        }
    }

    return "oops"
}

fun run_b(input: List<String>): String {
    val numbers = input.first().split(",").map(String::toInt)
    var boards = input.drop(1).filter { it.trim() != "" }.windowed(5, 5).map { BingoBoard(it) }
    var lastBoard = boards[0]

    for (x in numbers) {
        boards.forEach { it.remove(x) }

        boards = boards.filterNot { it.hasWon() }
        if (boards.isEmpty()) {
            return (lastBoard.remainingNumSum() * x).toString()
        } else {
            lastBoard = boards[0]
        }
    }

    return "oops"
}

class BingoBoard(lines: List<String>) {
    private var board: List<List<Int?>>

    init {
        board = lines.map { it.trim().split(Regex("\\s+")).map(String::toIntOrNull) }.toList()
    }

    fun hasWon(): Boolean {
        return (0..4).any { index -> board.all { it[index] == null } || board[index].all { it == null } }
    }

    fun remove(x: Int) {
        board = board.map { it.map { if (it == x) null else it } }
    }

    fun remainingNumSum(): Int {
        return board.map { it.filterIsInstance<Int>().sum() }.sum()
    }

    fun print() {
        board.forEach { row -> println(row.joinToString()) }
        println()
    }
}