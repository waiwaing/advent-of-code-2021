package `16`

fun run_a(input: List<String>): String {
    val rootPacket = PacketFactory(input.first()).build()
    fun Packet.sumOfVersions(): Int = this.version + this.children.sumOf { it.sumOfVersions() }
    return rootPacket.sumOfVersions().toString()
}

fun run_b(input: List<String>): String {
    for (x in listOf(
//        "C200B40A82", "04005AC33890", "880086C3E88112", "CE00C43D881120", "D8005AC2A8F0",
//        "F600BC2D8F", "9C005AC2F8F0", "9C0141080250320F1802104A08",
        input[0]
    )) {
        val rootPacket = PacketFactory(x).build()
        println(rootPacket.value().toString())
    }

    return ""
}