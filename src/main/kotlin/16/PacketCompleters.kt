package `16`

fun interface PacketCompleter {
    fun isComplete(
        payload: List<Boolean>, children: MutableList<Packet>, childrenSizeInBits: Int?, input: List<Boolean>
    ): Pair<Boolean, Int?>
}

val Mode0PacketCompleter = PacketCompleter { payload, children, childrenSizeInBits, _ ->
    when {
        payload.size < 16 -> Pair(false, null)
        childrenSizeInBits == null -> Pair(false, payload.slice(1 until 16).toInt())
        payload.size < childrenSizeInBits + 16 -> Pair(false, childrenSizeInBits)
        else -> {
            val workingPayload = payload.drop(16).toMutableList()
            while (workingPayload.isNotEmpty()) {
                children.add(PacketFactory(workingPayload).build())
            }
            Pair(true, childrenSizeInBits)
        }
    }
}

val Mode1PacketCompleter = PacketCompleter { payload, children, childrenSizeInBits, input ->
    when {
        payload.size < 12 -> Pair(false, null)
        childrenSizeInBits == null -> {
            val childCount = payload.slice(1 until 12).toInt()
            val workingPayload = input.toMutableList()
            repeat(childCount) { children.add(PacketFactory(workingPayload).build()) }
            Pair(false, children.sumOf { it.bitArray.size })
        }
        payload.size == childrenSizeInBits + 12 -> Pair(true, childrenSizeInBits)
        else -> Pair(false, childrenSizeInBits)
    }
}

val LiteralPacketCompleter = PacketCompleter { payload, _, _, _ ->
    Pair(payload.windowed(5, 5, false).lastOrNull()?.get(0) == false, 0)
}
