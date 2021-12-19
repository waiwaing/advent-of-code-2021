package `18`

import `15`.cartesianProduct
import kotlin.math.ceil
import kotlin.math.floor

fun run_a(input: List<String>) =
    input.map(::SfNumber).reduce { x, y -> SfNumber("[$x,$y]").apply { this.reduce() } }.magnitude().toString()

fun run_b(input: List<String>) =
    input.map(::SfNumber).cartesianProduct(input.map(::SfNumber))
        .maxOf { (x, y) -> SfNumber("[${x},${y}]").apply { this.reduce() }.magnitude() }.toString()

class SfNumber(var parent: SfNumber?, var left: SfNumber?, var right: SfNumber?, var value: Int?) {
    private var buildStringRemainder: String = ""

    init {
        left?.parent = this
        right?.parent = this
    }

    constructor(buildString: String) : this(null, buildString)
    constructor(parent: SfNumber?, buildString: String) : this(parent, null, null, null) {
        buildStringRemainder = buildSfNumber(buildString)
    }

    private fun buildSfNumber(input: String): String = when (input.first()) {
        '[' -> {
            left = SfNumber(this, input.substring(1))
            right = SfNumber(this, left!!.buildStringRemainder)
            value = null
            right!!.buildStringRemainder
        }
        in '0'..'9' -> {
            left = null
            right = null
            value = input.takeWhile { it in '0'..'9' }.toInt()
            input.dropWhile { it in '0'..'9' }
        }
        else -> buildSfNumber(input.substring(1))
    }

    fun reduce() {
        while (explode(4) || split()) Unit
    }

    private fun explode(nestsToGo: Int): Boolean = when {
        value != null -> false
        nestsToGo >= 1 -> left!!.explode(nestsToGo - 1) || right!!.explode(nestsToGo - 1)
        else -> {
            getAdjacentLeafNode(true)?.let { it.value = it.value!! + left!!.value!! }
            getAdjacentLeafNode(false)?.let { it.value = it.value!! + right!!.value!! }

            buildSfNumber("0")
            true
        }
    }

    private fun split(): Boolean = when (value) {
        null -> left!!.split() || right!!.split()
        in 0..9 -> false
        else -> {
            buildSfNumber("[${floor(value!! / 2.0).toInt()},${ceil(value!! / 2.0).toInt()}]")
            true
        }
    }

    fun magnitude(): Long = value?.toLong() ?: (3 * left!!.magnitude() + 2 * right!!.magnitude())

    private fun getLeftMostChild(): SfNumber = value?.let { this } ?: left!!.getLeftMostChild()
    private fun getRightMostChild(): SfNumber = value?.let { this } ?: right!!.getRightMostChild()

    private fun getAdjacentLeafNode(toLeft: Boolean): SfNumber? = when {
        value != null -> this
        parent == null -> null
        toLeft && this == parent?.right -> parent?.left?.getRightMostChild()
        !toLeft && this == parent?.left -> parent?.right?.getLeftMostChild()
        else -> parent?.getAdjacentLeafNode(toLeft)
    }

    override fun toString(): String = value?.toString() ?: "[${left},${right}]"
}
