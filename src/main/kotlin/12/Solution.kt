package `12`

fun run_a(input: List<String>): String {
    val graph = inputToGraph(input)

    val startNode = graph.nodes["start"]!!
    val endNode = graph.nodes["end"]!!

    val unexhausted = mutableListOf<Pair<List<Node>, Iterator<Node>>>()
    unexhausted.add(Pair(listOf(startNode), startNode.edges.iterator()))

    val paths = mutableListOf<List<Node>>()

    while (unexhausted.isNotEmpty()) {
        val (pathToDate, iterator) = unexhausted.first()
        if (!iterator.hasNext()) {
            unexhausted.removeFirst()
            continue
        }

        val chosenNode = iterator.next()

        if (chosenNode.name.lowercase() == chosenNode.name && chosenNode.name in pathToDate.map { it.name }) {
            continue
        }

        val path = pathToDate.plus(chosenNode)

        if (chosenNode == endNode) {
            paths.add(path)
        } else {
            unexhausted.add(0, Pair(path, chosenNode.edges.iterator()))
        }
    }

    return paths.count().toString()
}

fun run_b(input: List<String>): String {
    val graph = inputToGraph(input)

    val startNode = graph.nodes["start"]!!
    val endNode = graph.nodes["end"]!!

    val unexhausted = mutableListOf<Pair<List<Node>, Iterator<Node>>>()
    unexhausted.add(Pair(listOf(startNode), startNode.edges.iterator()))

    val paths = mutableListOf<List<Node>>()

    while (unexhausted.isNotEmpty()) {
        val (pathToDate, iterator) = unexhausted.first()
        if (!iterator.hasNext()) {
            unexhausted.removeFirst()
            continue
        }

        val chosenNode = iterator.next()

        if (chosenNode.name.lowercase() == chosenNode.name && pathToDate.count { it == chosenNode } != 0) {
            val visitedSmallCaves = pathToDate.filter { it.name.lowercase() == it.name }

            if (chosenNode == startNode || chosenNode == endNode ||
                visitedSmallCaves.toSet().toList() != visitedSmallCaves ||
                pathToDate.count { it == chosenNode } > 1
            ) {
                continue
            }
        }

        val path = pathToDate.plus(chosenNode)

        if (chosenNode == endNode) {
            paths.add(path)
        } else {
            unexhausted.add(0, Pair(path, chosenNode.edges.iterator()))
        }
    }

    return paths.count().toString()
}

fun inputToGraph(input: List<String>): Graph {
    val graph = Graph()
    input.forEach {
        val (a, b) = it.split("-")
        val nodeA = graph.nodes.getOrPut(a) { Node(a) }
        val nodeB = graph.nodes.getOrPut(b) { Node(b) }

        nodeA.edges.add(nodeB)
        nodeB.edges.add(nodeA)
    }

    return graph
}

data class Node(val name: String) {
    val edges = mutableSetOf<Node>()
}

class Graph {
    val nodes = mutableMapOf<String, Node>()
}