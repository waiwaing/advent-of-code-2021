package `23`

import java.util.*
import kotlin.math.pow

fun run_a(input: List<String>): String {
    val startingState: GameState = input[0]
    val targetState: GameState = "...........AA........BB........CC........DD.."

    return runDijkstra(startingState, targetState, 2)
}

fun run_b(input: List<String>): String {
    val startingState: GameState = input[0].mapIndexed { i, ch ->
        when (i) {
            12, 13 -> 'D'
            22, 43 -> 'C'
            23, 32 -> 'B'
            33, 42 -> 'A'
            14, 24, 34, 44 -> input[0][i - 2]
            else -> ch
        }
    }.joinToString("")
    val targetState: GameState = "...........AAAA......BBBB......CCCC......DDDD"

    return runDijkstra(startingState, targetState, 4)
}

fun runDijkstra(startingState: String, targetState: String, roomSize: Int): String {
    val seenStates = mutableSetOf<GameState>()
    val costs = mutableMapOf<GameState, Int>().withDefault { Int.MAX_VALUE }
    val pq = PriorityQueue<GameState> { s1, s2 -> costs.getValue(s1) - costs.getValue(s2) }

    pq.add(startingState)
    costs[startingState] = 0

    while (pq.isNotEmpty()) {
        val state = pq.remove()
        seenStates.add(state)

        if (state == targetState) break
        val graph = Graph(state, 10 + roomSize, 20 + roomSize, 30 + roomSize, 40 + roomSize)

        graph.getPotentialMoves().forEach { move ->
            val newState = graph.move(move)
            if (newState in seenStates) return@forEach

            val newCost = costs[state]!! + move.cost
            if (costs[newState] == null || newCost < costs[newState]!!) {
                costs[newState] = newCost
                pq.add(newState)
            }
        }
    }

    return costs[targetState].toString()
}

data class Node(val id: Int, val bug: Char?) {
    var edges: MutableList<Pair<Node, Int>> = mutableListOf()
    override fun toString() = "$id to $bug"
}

data class Move(val from: Node, val to: Node, val cost: Int)
typealias GameState = String

data class Graph(val locations: GameState, val maxA: Int, val maxB: Int, val maxC: Int, val maxD: Int) {
    private val graph = locations.mapIndexed { i, ch -> Node(i, if (ch == '.') null else ch) }.associateBy { it.id }

    private fun addEdge(ix1: Int, ix2: Int, distance: Int) {
        graph[ix1]!!.edges.add(Pair(graph[ix2]!!, distance))
        graph[ix2]!!.edges.add(Pair(graph[ix1]!!, distance))
    }

    init {
        // Corridor
        addEdge(0, 1, 1); addEdge(1, 2, 2); addEdge(2, 3, 2)
        addEdge(3, 4, 2); addEdge(4, 5, 2); addEdge(5, 6, 1)

        // Rooms
        addEdge(11, 12, 1); addEdge(12, 13, 1); addEdge(13, 14, 1)
        addEdge(21, 22, 1); addEdge(22, 23, 1); addEdge(23, 24, 1)
        addEdge(31, 32, 1); addEdge(32, 33, 1); addEdge(33, 34, 1)
        addEdge(41, 42, 1); addEdge(42, 43, 1); addEdge(43, 44, 1)

        // Doorways
        addEdge(1, 11, 2); addEdge(2, 11, 2)
        addEdge(2, 21, 2); addEdge(3, 21, 2)
        addEdge(3, 31, 2); addEdge(4, 31, 2)
        addEdge(4, 41, 2); addEdge(5, 41, 2)
    }

    private fun nonBlockedMovesFrom(from: Node): Map<Node, Int> {
        val queue = mutableListOf(Pair(from, 0))
        val results = mutableMapOf<Node, Int>()

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            current.first.edges.filter { (to, _) -> to.bug == null && to !in results }
                .map { it.first to current.second + it.second }
                .also { queue.addAll(it) }
                .toMap(results)
        }

        return results
    }

    private val aRange = 11..maxA
    private val bRange = 21..maxB
    private val cRange = 31..maxC
    private val dRange = 41..maxD

    fun getPotentialMoves() =
        graph.filterValues { it.bug != null }.flatMap { (from, fromNode) ->
            val curBug = fromNode.bug

            nonBlockedMovesFrom(fromNode)
                .filter { (toNode, _) ->
                    val to = toNode.id

                    when {
                        to > 10 && to !in aRange && to !in bRange && to !in cRange && to !in dRange -> false
                        from < 10 && to < 10 -> false // no hallway to hallway
                        from > 10 && to > 10 -> false // no room to room

                        // (1) bugs only allowed to correct destinations
                        // (2) bugs only allowed in if destination has no wrong bugs
                        // (3) bugs only allowed in to furthest-most vacant spot
                        to in aRange && curBug != 'A' -> false
                        to in aRange && graph.filterKeys { it in aRange }.values.any { (it.bug ?: 'A') != 'A' } -> false
                        to in aRange && graph.filterKeys { it in to + 1..maxA }.values.any { it.bug == null } -> false

                        to in bRange && curBug != 'B' -> false
                        to in bRange && graph.filterKeys { it in bRange }.values.any { (it.bug ?: 'B') != 'B' } -> false
                        to in bRange && graph.filterKeys { it in to + 1..maxB }.values.any { it.bug == null } -> false

                        to in cRange && curBug != 'C' -> false
                        to in cRange && graph.filterKeys { it in cRange }.values.any { (it.bug ?: 'C') != 'C' } -> false
                        to in cRange && graph.filterKeys { it in to + 1..maxC }.values.any { it.bug == null } -> false

                        to in dRange && curBug != 'D' -> false
                        to in dRange && graph.filterKeys { it in dRange }.values.any { (it.bug ?: 'D') != 'D' } -> false
                        to in dRange && graph.filterKeys { it in to + 1..maxD }.values.any { it.bug == null } -> false

                        // bugs not allowed to leave once in correct position
                        from in aRange && curBug == 'A' &&
                                graph.filterKeys { it in from..maxA }.values.all { it.bug == 'A' } -> false
                        from in bRange && curBug == 'B' &&
                                graph.filterKeys { it in from..maxB }.values.all { it.bug == 'B' } -> false
                        from in cRange && curBug == 'C' &&
                                graph.filterKeys { it in from..maxC }.values.all { it.bug == 'C' } -> false
                        from in dRange && curBug == 'D' &&
                                graph.filterKeys { it in from..maxD }.values.all { it.bug == 'D' } -> false
                        else -> true
                    }
                }
                .map { (toNode, cost) -> Move(fromNode, toNode, cost * bugCost(curBug)) }
        }

    fun move(move: Move): GameState = graph.toSortedMap()
        .map { (position, node) ->
            when (position) {
                move.from.id -> '.'
                move.to.id -> move.from.bug
                else -> node.bug ?: '.'
            }
        }.joinToString("")
}

fun bugCost(bug: Char?): Int = (10.0.pow(bug!! - 'A')).toInt()

/*
0
...........BA........CD........BC........DA
40
..B........BA........CD.........C........DA
240
..BC.......BA.........D.........C........DA
440
..B........BA.........D........CC........DA


A.....D....BDDA......CCBD......BBAC........CA
A....BD....BDDA......CCBD.......BAC........CA
A...BBD....BDDA......CCBD........AC........CA
AA..BBD....BDDA......CCBD.........C........CA

 */