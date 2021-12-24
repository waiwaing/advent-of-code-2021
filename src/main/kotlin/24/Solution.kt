package `24`

fun run_a(input: List<String>): String {
   // (0..127).map { it.toString(2).padStart(7, '0').toList().map { it.toString().toLong() } }
     listOf(listOf(1L, 1, 1, 1, 1, 1, 1, 1))
        .forEach {
            val res = runMonad(input, "ABCDEFGHIJKLMN".iterator(), it)
            println(res[3])
        }

    return ""
}

fun run_b(input: List<String>): String {
    return ""
}

fun runMonad(program: List<String>, input: Iterator<Char>, rules: List<Long>): List<Node> {
    val vars: MutableList<Node> = mutableListOf(ConstantNode(0), ConstantNode(0), ConstantNode(0), ConstantNode(0))

    program.forEach { line ->
        val command = line.substring(0..2)
        val aIndex = line[4] - 'w'

        val arg1Node = vars[aIndex]

        val arg2Node = when (val arg2 = if (line.length > 6) line.substring(6) else "") {
            "w", "x", "y", "z" -> vars[arg2[0] - 'w']
            "" -> NeverNode(arg2)
            else -> ConstantNode(arg2.toLong())
        }

        vars[aIndex] = when {
            command == "inp" -> InpNode(input.next())
            command == "add" -> AddNode(arg1Node, arg2Node)
            command == "mul" -> MulNode(arg1Node, arg2Node)
            command == "div" -> DivNode(arg1Node, arg2Node)
            command == "mod" -> ModNode(arg1Node, arg2Node)
            command == "eql" -> EqlNode(arg1Node, arg2Node, rules)
            else -> throw RuntimeException()
        }.simplify()
    }

    return vars
}

