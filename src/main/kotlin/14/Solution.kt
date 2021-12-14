package `14`

fun run_a(input: List<String>): String {
    val rules = input
        .subList(2, input.size)
        .associate { val (x, y) = it.split("->"); Pair(x.trim(), y.trim()) }

    var template = input.first()

    repeat(10) {
        template = template
            .zipWithNext { a, b -> listOf(a, rules.getOrDefault(a.plus(b.toString()), "")) }
            .flatten()
            .joinToString("")
            .plus(template.last())
    }

    val counts = template.groupBy { it }.mapValues { it.value.size }

    return (counts.maxOf { it.value } - counts.minOf { it.value }).toString()
}

fun run_b(input: List<String>): String {
    val rules = input
        .subList(2, input.size)
        .associate { val (x, y) = it.split("->"); Pair(x.trim(), y.trim()) }

    var pairs = input.first()
        .zipWithNext()
        .map { it.first.plus(it.second.toString()) }
        .groupBy { it }.mapValues { it.value.size.toULong() }

    repeat(40) {
        pairs = pairs
            .flatMap { (key, ct) ->
                val intermediate = rules.getValue(key)
                listOf(Pair(key[0] + intermediate, ct), Pair(intermediate + key[1], ct))
            }
            .groupBy { it.first }
            .mapValues { it.value.sumOf { it.second } }
    }

    val counts = pairs
        .flatMap { listOf(Pair(it.key[0], it.value), Pair(it.key[1], it.value)) }
        .groupBy { it.first }
        .mapValues { it.value.sumOf { it.second } / 2UL }
        .toMutableMap()

    counts[input.first().first()] = counts[input.first().first()]!! + 1UL
    counts[input.first().last()] = counts[input.first().last()]!! + 1UL

    return (counts.maxOf { it.value } - counts.minOf { it.value }).toString()
}