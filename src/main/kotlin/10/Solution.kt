package `10`

fun run_a(input: List<String>): String {
    return input.sumOf { line ->
        val stack = mutableListOf<Char>()
        line.forEach { char ->
            when (char) {
                '[', '(', '<', '{' -> stack.add(char)
                ']' -> if (stack.removeLast() != '[') return@sumOf 57L
                ')' -> if (stack.removeLast() != '(') return@sumOf 3L
                '>' -> if (stack.removeLast() != '<') return@sumOf 25137L
                '}' -> if (stack.removeLast() != '{') return@sumOf 1197L
                else -> throw RuntimeException("Unexpected character $char")
            }
        }

        0L
    }.toString()
}

fun run_b(input: List<String>): String {
    return input
        .map { line ->
            val stack = mutableListOf<Char>()
            line.forEach { char ->
                when (char) {
                    '[', '(', '<', '{' -> stack.add(char)
                    ']' -> if (stack.removeLast() != '[') return@map listOf<Char>()
                    ')' -> if (stack.removeLast() != '(') return@map listOf<Char>()
                    '>' -> if (stack.removeLast() != '<') return@map listOf<Char>()
                    '}' -> if (stack.removeLast() != '{') return@map listOf<Char>()
                    else -> throw RuntimeException("Unexpected character $char")
                }
            }

            stack
        }
        .filter(List<Char>::isNotEmpty)
        .map {
            var score = 0L
            it.reversed().forEach { char ->
                score = score * 5 + when (char) {
                    '(' -> 1
                    '{' -> 3
                    '<' -> 4
                    '[' -> 2
                    else -> throw RuntimeException("Unexpected character $char")
                }
            }

            score
        }
        .sorted()
        .let { it[it.size / 2] }
        .toString()
}

// 335579505