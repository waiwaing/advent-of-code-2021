package `7`

import kotlin.math.abs

fun run_a(input: List<String>): String {
    val crabs = input.first().split(",").map(String::toInt)

    val min = crabs.minOrNull()!!
    val max = crabs.maxOrNull()!!

    val optimalPosition = (min..max).minByOrNull {
        crabs.map { crab -> abs(it - crab) }.sum()
    }!!

    val answer = crabs.map { abs(it - optimalPosition) }.sum()

    return answer.toString()
}

fun run_b(input: List<String>): String {
    val crabs = input.first().split(",").map(String::toInt)

    val min = crabs.minOrNull()!!
    val max = crabs.maxOrNull()!!

    val optimalPosition = (min..max).minByOrNull {
        crabs.sumOf { crab ->
            val dist = abs(it - crab)
            dist * (dist + 1) / 2
        }
    }!!

    val answer = crabs.sumOf { crab ->
        val dist = abs(optimalPosition - crab)
        dist * (dist + 1) / 2
    }

    return answer.toString()
}