package `3`

fun run_a(input: List<String>): String {
    val values = input.map(String::toCharArray)

    val gamma = (0 until values[0].size).map { i ->
        values.map { it[i] }.groupingBy { it }.eachCount().maxByOrNull { it.value }!!.key
    }.joinToString("")

    val epsilon = gamma.map { if (it == '1') '0' else '1' }.joinToString("")

    return (gamma.toInt(2) * epsilon.toInt(2)).toString()
}

fun run_b(input: List<String>): String {
    val values = input.map(String::toCharArray)

    var candidates = values.toList()
    var bitPosition = 0

    while (candidates.size > 1) {
        candidates = candidates.filter {
            val count = candidates.map { it[bitPosition] }.groupingBy { it }.eachCount()
            it[bitPosition] == if (count['1']!! >= count['0']!!) '1' else '0'
        }
        bitPosition += 1
    }

    val oxygen = candidates.single().joinToString("").toInt(2)

    candidates = values.toList()
    bitPosition = 0

    while (candidates.size > 1) {
        candidates = candidates.filter {
            val count = candidates.map { it[bitPosition] }.groupingBy { it }.eachCount()
            it[bitPosition] == if (count['1']!! >= count['0']!!) '0' else '1'
        }
        bitPosition += 1
    }

    val co2 = candidates.single().joinToString("").toInt(2)

    return (oxygen * co2).toString()
}