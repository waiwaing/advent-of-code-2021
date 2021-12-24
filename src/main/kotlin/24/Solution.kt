package `24`

fun run_a(input: List<String>): String {
    // (0..127).map { it.toString(2).padStart(7, '0').toList().map { it.toString().toLong() } }
    listOf(listOf(1L, 1, 1, 1, 1, 1, 1, 1))
        .forEach { println(runMonad(input, "ABCDEFGHIJKLMN".iterator(), it)[3]) }

    return ""
}

fun run_b(input: List<String>): String {
    return ""
}

fun runMonad(program: List<String>, input: Iterator<Char>, rules: List<Long>): List<Node> {
    val vars: MutableList<Node> = mutableListOf(ConNode(0), ConNode(0), ConNode(0), ConNode(0))

    program.forEach { line ->
        val command = line.substring(0..2)
        val arg1Index = line[4] - 'w'
        val arg1Node = vars[arg1Index]
        val arg2Node = when (val arg2 = if (line.length > 6) line.substring(6) else "") {
            "w", "x", "y", "z" -> vars[arg2[0] - 'w']
            "" -> NeverNode()
            else -> ConNode(arg2.toLong())
        }

        vars[arg1Index] = when (command) {
            "inp" -> InpNode(input.next())
            "add" -> AddNode(arg1Node, arg2Node)
            "mul" -> MulNode(arg1Node, arg2Node)
            "div" -> DivNode(arg1Node, arg2Node)
            "mod" -> ModNode(arg1Node, arg2Node)
            "eql" -> EqlNode(arg1Node, arg2Node, rules)
            else -> throw Exception("unexpected command")
        }.simplify()
    }

    return vars
}
