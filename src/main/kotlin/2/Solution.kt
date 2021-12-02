package `2`

fun run_a(input: List<String>): String {
    val values = input
        .map { Pair(it.substringBefore(' '), it.substringAfter(' ').toInt()) }
        .groupBy { it.first }
        .mapValues { it.value.sumOf { it.second } }

    val depth = values["down"]!! - values["up"]!!
    val horiz = values["forward"]!!

    return (depth * horiz).toString()
}

fun run_b(input: List<String>): String {
    val values = input
        .map { Pair(it.substringBefore(' '), it.substringAfter(' ').toInt()) }

    var depth = 0
    var aim = 0
    var position = 0

    values.forEach {
        when (it.first) {
            "down" -> aim += it.second
            "up" -> aim -= it.second
            "forward" -> {
                position += it.second
                depth += aim * it.second
            }
        }
    }

    return (depth * position).toString()
}