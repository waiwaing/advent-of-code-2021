package `16`

fun Char.toBooleanList() = Integer.toBinaryString(this.digitToInt(16))
    .padStart(4, '0')
    .map { it == '1' }

class PacketFactory(val input: MutableList<Boolean>) {
    fun build(): Packet = when {
        input.slice(3..5).toInt() == 4 -> LiteralPacket(input)
        else -> Packet(
            input,
            if (input[6]) Mode1PacketCompleter else Mode0PacketCompleter,
            when (input.slice(3..5).toInt()) {
                0 -> PacketOperator { it.sumOf { it.value() } }
                1 -> PacketOperator { it.fold(1) { acc, x -> x.value() * acc } }
                2 -> PacketOperator { it.minOf { it.value() } }
                3 -> PacketOperator { it.maxOf { it.value() } }
                5 -> PacketOperator { if (it.first().value() > it.last().value()) 1 else 0 }
                6 -> PacketOperator { if (it.first().value() < it.last().value()) 1 else 0 }
                7 -> PacketOperator { if (it.first().value() == it.last().value()) 1 else 0 }
                else -> null
            }
        )
    }

    constructor(input: String) : this(input.flatMap(Char::toBooleanList).toMutableList())
}
