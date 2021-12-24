package `24`

open class Node {
    open fun hasValueGreaterThanNine() = false
    open fun simplify(): Node = this
}

data class ConNode(val value: Long) : Node() {
    override fun toString() = value.toString()
    override fun hasValueGreaterThanNine() = value > 9
}

data class PnMap(val data: Map<Char, Long> = mapOf()) : HashMap<Char, Long>(data) {
    fun merge(other: PnMap) = PnMap((keys + other.keys).associateWith { (this[it] ?: 0) + (other[it] ?: 0) })
    fun multiplyAll(multiplicand: Long) = PnMap(mapValues { it.value * multiplicand })
    fun divideAll(divisor: Long) = PnMap(mapValues { (input, coefficient) ->
        when {
            coefficient % divisor == 0L -> coefficient / divisor
            input != ' ' && coefficient * 9 <= divisor -> 0
            input == ' ' -> coefficient / divisor // assumes this is the only non-integer division
            else -> throw Exception(":(")
        }
    })
}

data class PolNode(val poly: PnMap) : Node() {
    override fun toString() = poly.map { (k, v) -> v.let { if (it == 1L) "" else it.toString() } + k.toString().trim() }
        .joinToString(" + ")
        .let { "($it)" }

    override fun hasValueGreaterThanNine() = (poly[' '] ?: 0) > 9
}

data class AddNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 + $child2)"
    override fun hasValueGreaterThanNine() = child1.hasValueGreaterThanNine() || child2.hasValueGreaterThanNine()

    override fun simplify(): Node = when {
        child1 is ConNode && child2 is ConNode -> ConNode(child1.value + child2.value)
        child1 is ConNode && child1.value == 0L -> child2
        child1 is PolNode && child2 is PolNode -> PolNode(child1.poly.merge(child2.poly))
        child1 is PolNode && child2 is ConNode -> PolNode(child1.poly.merge(PnMap(mapOf(' ' to child2.value))))
        child1 is InpNode && child2 is ConNode -> PolNode(PnMap(mapOf(child1.value to 1, ' ' to child2.value)))
        else -> AddNode(child1.simplify(), child2.simplify())
    }
}

data class MulNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 * $child2)"

    override fun simplify(): Node = when {
        child1 is ConNode && child2 is ConNode -> ConNode(child1.value * child2.value)
        child2 is ConNode && child2.value == 0L -> ConNode(0)
        child1 is PolNode && child2 is ConNode -> PolNode(PnMap(child1.poly.multiplyAll(child2.value)))
        else -> MulNode(child1.simplify(), child2.simplify())
    }
}

data class DivNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 / $child2)"

    override fun simplify() = when {
        child1 is ConNode && child2 is ConNode -> ConNode(child1.value / child2.value)
        child1 is PolNode && child2 is ConNode -> PolNode(child1.poly.divideAll(child2.value))
        else -> this
    }
}

data class ModNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 % $child2)"

    override fun simplify() = when {
        child1 is PolNode && child2 is ConNode ->
            PolNode(PnMap(child1.poly.filterValues { it % child2.value != 0L }.mapValues { it.value % child2.value }))
        else -> this
    }
}

data class EqlNode(val child1: Node, val child2: Node, val rules: List<Long>) : Node() {
    override fun toString() = "($child1 == $child2)"

    override fun simplify() = when {
        child1 is ConNode && child2 is ConNode -> ConNode(if (child1.value == child2.value) 1 else 0)
        child2 is InpNode && child1.hasValueGreaterThanNine() -> ConNode(0)
        child2 == InpNode('E') && child1 is PolNode -> ConNode(rules[0]).also { println("$child1 = E") }
        child2 == InpNode('H') && child1 is PolNode -> ConNode(rules[1]).also { println("$child1 = H") }
        child2 == InpNode('J') && child1 is PolNode -> ConNode(rules[2]).also { println("$child1 = J") }
        child2 == InpNode('K') && child1 is PolNode -> ConNode(rules[3]).also { println("$child1 = K") }
        child2 == InpNode('L') && child1 is PolNode -> ConNode(rules[4]).also { println("$child1 = L") }
        child2 == InpNode('M') && child1 is PolNode -> ConNode(rules[5]).also { println("$child1 = M") }
        child2 == InpNode('N') && child1 is PolNode -> ConNode(rules[6]).also { println("$child1 = N") }
        else -> this
    }
}

data class InpNode(val value: Char) : Node() {
    override fun toString() = value.toString()
}

class NeverNode : Node()