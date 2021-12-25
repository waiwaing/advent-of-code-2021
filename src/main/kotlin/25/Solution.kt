package `25`

fun run_a(input: List<String>): String {
    var grid = input.map { line -> line.toList() }
    var steps = 0

    do {
        var movement = false
        val t1 = grid.map { line -> line.toMutableList() }
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                val newX = if (x + 1 >= row.size) 0 else x + 1
                if (char == '>' && grid[y][newX] == '.') {
                    t1[y][newX] = '>'
                    t1[y][x] = '.'
                    movement = true
                }
            }
        }

        val t2 = t1.map { line -> line.toMutableList() }
        t1.forEachIndexed l1@{ y, row ->
            row.forEachIndexed l2@{ x, char ->
                val newY = if (y + 1 >= t1.size) 0 else y + 1
                if (char == 'v' && t1[newY][x] == '.') {
                    t2[newY][x] = 'v'
                    t2[y][x] = '.'
                    movement = true
                }

            }
        }

        steps += 1
        grid = t2
    } while (movement)

    return steps.toString()
}

fun run_b(input: List<String>): String {
    return ""
}

fun printGrid(grid: List<List<Char>>) = grid.forEach { println(it.joinToString("")) }.also { println() }