package `1`

fun run_a(input: List<String>): String {
    return input.map(String::toInt).zipWithNext().count { it.second > it.first }.toString()
}

fun run_b(input: List<String>): String {
    return input.map(String::toInt)
        .windowed(3, 1, false)
        .map(List<Int>::sum)
        .zipWithNext()
        .count { it.second > it.first }
        .toString()
}


