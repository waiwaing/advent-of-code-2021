package `8`

typealias EncodingMap = Map<Char, Char>

fun run_a(input: List<String>): String {
    return input.sumOf {
        it
            .substringAfter("|")
            .trim()
            .split(" ")
            .count { it.length in arrayOf(2, 3, 4, 7) }
    }.toString()
}

fun run_b(input: List<String>): String {
    return input.sumOf {
        val patterns = it.substringBefore("|").trim().split(" ")
        val encodingMap = patternsToEncodingMap(patterns)

        it.substringAfter("|").trim().split(" ")
            .map { numFromCodedSegments(it, encodingMap) }
            .joinToString("")
            .toInt()
    }.toString()
}

fun numFromCodedSegments(codedSegments: String, encodingMap: EncodingMap): Int {
    val uncodedSegments = codedSegments.map { encodingMap[it] }.toSet()
    return when (uncodedSegments) {
        "ABCEFG".toSet() -> 0
        "CF".toSet() -> 1
        "ACDEG".toSet() -> 2
        "ACDFG".toSet() -> 3
        "BCDF".toSet() -> 4
        "ABDFG".toSet() -> 5
        "ABDEFG".toSet() -> 6
        "ACF".toSet() -> 7
        "ABCDEFG".toSet() -> 8
        "ABCDFG".toSet() -> 9
        else -> throw Exception(uncodedSegments.toString())
    }
}

fun patternsToEncodingMap(patterns: List<String>): EncodingMap {
    val codedAToG = 'a'..'g'
    val possibilities = codedAToG.associateWith { mutableSetOf('A', 'B', 'C', 'D', 'E', 'F', 'G') }

    val reducePossibilities = { codedSegments: List<Char>, uncodedSegments: List<Char> ->
        val x = codedAToG.partition { it in codedSegments }
        x.first.forEach { c -> possibilities[c]?.removeIf { it !in uncodedSegments } }
        x.second.forEach { c -> possibilities[c]?.removeIf { it in uncodedSegments } }
    }

    val patternsByLength = patterns.groupBy { it.length }
    val twiceMissingInFives = codedAToG
        .filter { codedSegment -> patternsByLength[5]!!.count { codedSegment !in it } == 2 }
    val alwaysInSixes = codedAToG
        .filter { codedSegment -> patternsByLength[6]!!.all { codedSegment in it } }

    reducePossibilities(patternsByLength[2]!!.single().toList(), listOf('C', 'F'))
    reducePossibilities(patternsByLength[3]!!.single().toList(), listOf('A', 'C', 'F'))
    reducePossibilities(patternsByLength[4]!!.single().toList(), listOf('B', 'D', 'C', 'F'))
    reducePossibilities(twiceMissingInFives, listOf('B', 'E'))
    reducePossibilities(alwaysInSixes, listOf('A', 'B', 'F', 'G'))

    return possibilities.mapValues { it.value.single() }
}