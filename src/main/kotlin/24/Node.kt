package `24`

import java.lang.RuntimeException

data class PnMap(val data: Map<Char, Long> = mapOf()) : HashMap<Char, Long>(data) {
    fun merge(other: PnMap) = PnMap((keys + other.keys).associateWith { (this[it] ?: 0) + (other[it] ?: 0) })
    fun multiplyAll(multiplicand: Long) = PnMap(mapValues { it.value * multiplicand })
    fun divideAll(dividend: Long) = PnMap(mapValues { (input, coefficient) ->
        when {
            coefficient % dividend == 0L -> coefficient / dividend
            input != ' ' && coefficient <= 2 -> 0
            input == ' ' -> coefficient / dividend // assumes this is the only non-integer division
            else -> throw RuntimeException(":(")
        }
    })
}


open class Node {
    open fun simplify(): Node = this
    open fun hasValueGreaterThanNine() = false
}

data class ConstantNode(val value: Long) : Node() {
    override fun toString() = value.toString()
    override fun hasValueGreaterThanNine() = value > 9
}

data class PolyNode(val poly: PnMap) : Node() {
    override fun toString() = "(" +
            (poly.map { (k, v) -> v.toString() + k.toString().let { if (it == " ") "" else it } }
                .joinToString(" + ")) + ")"

    override fun hasValueGreaterThanNine() = (poly[' '] ?: 0) > 9
}

data class AddNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 + $child2)"

    override fun simplify(): Node = when {
        child1 is ConstantNode && child2 is ConstantNode -> ConstantNode(child1.value + child2.value)
        child1 is ConstantNode && child1.value == 0L -> child2
        child2 is ConstantNode && child2.value == 0L -> child1
        child1 is PolyNode && child2 is PolyNode ->
            PolyNode(child1.poly.merge(child2.poly))
        child1 is PolyNode && child2 is ConstantNode ->
            PolyNode(child1.poly.merge(PnMap(mapOf(' ' to child2.value))))
        child1 is PolyNode && child2 is InpNode ->
            PolyNode(child1.poly.merge(PnMap(mapOf(child2.value to 1))))
        child2 is PolyNode && child1 is InpNode ->
            PolyNode(child2.poly.merge(PnMap(mapOf(child1.value to 1))))
        child1 is ConstantNode && child2 is InpNode ->
            PolyNode(PnMap(mapOf(child2.value to 1, ' ' to child1.value)))
        child2 is ConstantNode && child1 is InpNode ->
            PolyNode(PnMap(mapOf(child1.value to 1, ' ' to child2.value)))
        child1 is AddNode && child2 is ConstantNode && child1.child1 is ConstantNode ->
            AddNode(ConstantNode(child1.child1.value + child2.value), child1.child2).simplify()
        child1 is AddNode && child2 is ConstantNode && child1.child2 is ConstantNode ->
            AddNode(ConstantNode(child1.child2.value + child2.value), child1.child1).simplify()
        child1 is AddNode && child2 is AddNode -> AddNode(
            AddNode(child1.child1, child2.child1).simplify(),
            AddNode(child1.child2, child2.child2).simplify()
        )
        else -> AddNode(child1.simplify(), child2.simplify())
    }

    override fun hasValueGreaterThanNine() = child1.hasValueGreaterThanNine() || child2.hasValueGreaterThanNine()
}

data class MulNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 * $child2)"

    override fun simplify(): Node = when {
        child1 is ConstantNode && child2 is ConstantNode -> ConstantNode(child1.value * child2.value)
        child1 is ConstantNode && child1.value == 0L -> ConstantNode(0)
        child2 is ConstantNode && child2.value == 0L -> ConstantNode(0)
        child1 is ConstantNode && child1.value == 1L -> child2
        child2 is ConstantNode && child2.value == 1L -> child1
        child1 is ConstantNode && child2 is InpNode ->
            PolyNode(PnMap(mapOf(child2.value to child1.value)))
        child2 is ConstantNode && child1 is InpNode ->
            PolyNode(PnMap(mapOf(child1.value to child2.value)))
        child1 is PolyNode && child2 is ConstantNode ->
            PolyNode(PnMap(child1.poly.multiplyAll(child2.value)))
        child1 is AddNode && child2 is ConstantNode ->
            AddNode(MulNode(child1.child1, child2), MulNode(child1.child2, child2)).simplify()
        child1 is MulNode && child2 is ConstantNode && child1.child1 is ConstantNode ->
            MulNode(ConstantNode(child1.child1.value * child2.value), child1.child2).simplify()
        child1 is MulNode && child2 is ConstantNode && child1.child2 is ConstantNode ->
            MulNode(ConstantNode(child1.child2.value * child2.value), child1.child1).simplify()
        child1 is MulNode && child2 is ConstantNode ->
            MulNode(MulNode(child1.child1, child2), MulNode(child1.child2, child2)).simplify()
        else -> MulNode(child1.simplify(), child2.simplify())
    }
}

data class DivNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 / $child2)"

    override fun simplify() = when {
        child1 is ConstantNode && child2 is ConstantNode -> ConstantNode(child1.value / child2.value)
        child1 is ConstantNode && child1.value == 0L -> ConstantNode(0)
        child2 is ConstantNode && child2.value == 1L -> child1
        child1 is PolyNode && child2 is ConstantNode -> PolyNode(child1.poly.divideAll(child2.value))
        else -> this
    }
}

data class ModNode(val child1: Node, val child2: Node) : Node() {
    override fun toString() = "($child1 % $child2)"

    override fun simplify() = when {
        child1 is ConstantNode && child2 is ConstantNode -> ConstantNode(child1.value % child2.value)
        child1 is PolyNode && child2 is ConstantNode &&
                child1.poly.filterKeys { it != ' ' }.all { (_, v) -> v % child2.value == 0L } ->
            ConstantNode((child1.poly[' '] ?: 0) % child2.value)
        child1 is PolyNode && child2 is ConstantNode -> PolyNode(
            PnMap(child1.poly.filterValues { it % child2.value != 0L }.mapValues { it.value % child2.value })
        )
        else -> this
    }
}

data class EqlNode(val child1: Node, val child2: Node, val rules: List<Long>) : Node() {
    override fun toString() = "($child1 == $child2)"

    override fun simplify() = when {
        child1 is ConstantNode && child2 is ConstantNode -> ConstantNode(if (child1.value == child2.value) 1 else 0)
        child2 is InpNode && child1.hasValueGreaterThanNine() -> ConstantNode(0)
        child2 == InpNode('E') && child1 is PolyNode -> ConstantNode(rules[0]).also { println(child1) }
        child2 == InpNode('H') && child1 is PolyNode -> ConstantNode(rules[1]).also { println(child1) }
        child2 == InpNode('J') && child1 is PolyNode -> ConstantNode(rules[2]).also { println(child1) }
        child2 == InpNode('K') && child1 is PolyNode -> ConstantNode(rules[3]).also { println(child1) }
        child2 == InpNode('L') && child1 is PolyNode -> ConstantNode(rules[4]).also { println(child1) }
        child2 == InpNode('M') && child1 is PolyNode -> ConstantNode(rules[5]).also { println(child1) }
        child2 == InpNode('N') && child1 is PolyNode -> ConstantNode(rules[6]).also { println(child1) }
        else -> this
    }

}

data class InpNode(val value: Char) : Node() {
    override fun toString() = value.toString()
}

data class NeverNode(val x: Any) : Node() {
    override fun simplify(): Nothing = throw RuntimeException()
}