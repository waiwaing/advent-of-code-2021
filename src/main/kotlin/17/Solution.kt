package `17`

import `15`.cartesianProduct
import kotlin.math.sign

data class XyPair<T>(val x: T, val y: T)

fun run_a(input: List<String>): String {
    val inputInt = input.map(String::toInt)
    val targetArea = XyPair(inputInt[0]..inputInt[1], inputInt[2]..inputInt[3])

    var best = XyPair(0, 0)
    var candidate = XyPair(0, 0)
    var currentSign = 0
    var flag = 3000

    while (flag > 0) {
        val indicator = isTrajectoryForTarget(candidate, targetArea)
        when {
            indicator == 0 -> {
                best = candidate
                candidate = XyPair(0, candidate.y + 1)
                flag = 3000
            }
            (currentSign == 0) || (currentSign == indicator) -> candidate = XyPair(candidate.x + indicator, candidate.y)
            else -> {
                candidate = XyPair(0, candidate.y + 1)
                flag -= 1
            }
        }
        currentSign = indicator
    }

    return highestYReached(best).toString()
}

fun run_b(input: List<String>): String {
    val inputInt = input.map(String::toInt)
    val targetArea = XyPair(inputInt[0]..inputInt[1], inputInt[2]..inputInt[3])

    return (0..1000).cartesianProduct(-1000..250)
        .count { isTrajectoryForTarget(XyPair(it.first, it.second), targetArea) == 0 }.toString()
}

private fun calculateTrajectory(
    trajectory: XyPair<Int>, shouldStop: (position: XyPair<Int>, velocity: XyPair<Int>) -> Boolean
): XyPair<Int> {
    var position = XyPair(0, 0)
    var velocity = trajectory

    while (!shouldStop(position, velocity)) {
        position = XyPair(position.x + velocity.x, position.y + velocity.y)
        velocity = XyPair(velocity.x - velocity.x.sign, velocity.y - 1)
    }

    return position
}

private fun isTrajectoryForTarget(trajectory: XyPair<Int>, target: XyPair<IntRange>): Int {
    val position = calculateTrajectory(trajectory) { position, velocity ->
        (position.x in target.x && position.y in target.y) || // in target
                (position.y < target.y.first && velocity.y < 0 && position.x < target.x.last) || // dropped below target
                (position.x > target.x.last) // overshoot
    }

    return when {
        position.x in target.x && position.y in target.y -> 0
        position.x > target.x.last -> -1
        else -> 1
    }
}

private fun highestYReached(trajectory: XyPair<Int>) = calculateTrajectory(trajectory) { _, vel -> vel.y < 0 }.y