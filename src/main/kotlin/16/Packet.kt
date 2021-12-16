package `16`

fun Iterable<Boolean>.toBinaryString(): String = this.map { if (it) '1' else '0' }.joinToString("")
fun Iterable<Boolean>.toInt(): Int = this.toBinaryString().toInt(2)

fun interface PacketOperator {
    fun apply(packets: Iterable<Packet>): Long
}

open class Packet(
    private val input: MutableList<Boolean>,
    private val packetCompleter: PacketCompleter,
    private val packetOperator: PacketOperator?
) {
    val bitArray = mutableListOf<Boolean>()
    val children = mutableListOf<Packet>()
    private var childrenSizeInBits: Int? = null

    init {
        while (true) {
            val (complete, bits) = packetCompleter.isComplete(payload(), children, childrenSizeInBits, input)
            childrenSizeInBits = bits
            if (complete) break else bitArray.add(input.removeFirst())
        }
    }

    val version = bitArray.slice(0..2).toInt()
    val type = bitArray.slice(3..5).toInt()
    fun payload() = bitArray.drop(6)
    open fun value(): Long = packetOperator!!.apply(children)
}

class LiteralPacket(input: MutableList<Boolean>) : Packet(input, LiteralPacketCompleter, null) {
    override fun value() = payload()
        .windowed(5, 5, false)
        .joinToString("") { it.drop(1).toBinaryString() }
        .toLong(2)
}
