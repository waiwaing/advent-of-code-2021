package `6`

val default_map = (0..8).associateWith { 0L }

fun run_a(input: List<String>): String {
    val initialState = default_map +
            input.first().split(",").map(String::toInt)
                .groupBy { it }
                .mapValues { it.value.count().toLong() }

    val finalState = (1..80).fold(initialState) { state, _ ->
        state.mapValues {
            when (it.key) {
                6 -> state.getOrDefault(7, 0) + state.getOrDefault(0, 0)
                8 -> state.getOrDefault(0, 0)
                else -> state.getOrDefault(it.key + 1, 0)
            }
        }
    }

    return finalState.values.sum().toString()
}

fun run_b(input: List<String>): String {
    val initialState = default_map +
            input.first().split(",").map(String::toInt)
                .groupBy { it }
                .mapValues { it.value.count().toLong() }

    val finalState = (1..256).fold(initialState) { state, _ ->
        state.mapValues {
            when (it.key) {
                6 -> state.getOrDefault(7, 0) + state.getOrDefault(0, 0)
                8 -> state.getOrDefault(0, 0)
                else -> state.getOrDefault(it.key + 1, 0)
            }
        }
    }

    return finalState.values.sum().toString()
}