package `19`

import `15`.cartesianProduct
import kotlin.math.abs
import kotlin.math.pow

fun run_a(input: List<String>) = buildMasterBeaconMap(input).first.size.toString()

fun run_b(input: List<String>): String {
    val (_, scanners) = buildMasterBeaconMap(input)
    return scanners.cartesianProduct(scanners).maxOf { (a, b) ->
        abs(a.first - b.first) + abs(a.second - b.second) + abs(a.third - b.third)
    }.toString()
}

fun buildMasterBeaconMap(input: List<String>): Pair<Set<Beacon>, Set<Scanner>> {
    val masterBeaconMap = mutableSetOf<Beacon>()
    val scanners = mutableSetOf<Scanner>()

    val unincludedBeaconSets = input.fold(mutableListOf<MutableList<String>>()) { acc, elem ->
        when {
            elem == "" -> Unit
            elem.startsWith("---") -> acc.add(mutableListOf())
            else -> acc.last().add(elem)
        }
        acc
    }.map(::BeaconSet).toMutableSet()
    val includedBeaconSets = mutableListOf(unincludedBeaconSets.first())

    masterBeaconMap.addAll(unincludedBeaconSets.first().beacons)

    while (includedBeaconSets.isNotEmpty()) {
        val toProcess = unincludedBeaconSets.filter { includedBeaconSets.first().overlappingBeacons(it).isNotEmpty() }

        if (toProcess.isEmpty()) {
            includedBeaconSets.removeFirst()
        } else {
            toProcess.forEach {
                val orientedBeaconSet = it.orientateToMap(masterBeaconMap)
                masterBeaconMap.addAll(orientedBeaconSet.beacons)
                orientedBeaconSet.scanner?.let { scanners.add(it) }

                unincludedBeaconSets.remove(it)
                includedBeaconSets.add(it)
            }
        }
    }

    return Pair(masterBeaconMap, scanners)
}

typealias Scanner = Triple<Long, Long, Long>

fun Long.square() = this * this

data class Beacon(val x: Long, val y: Long, val z: Long) {
    val neighbors = mutableListOf<Pair<Beacon, Double>>()
    val distances by lazy { neighbors.map { it.second } }

    fun distanceTo(b: Beacon) = ((b.x - x).square() + (b.y - y).square() + (b.z - z).square()).toDouble().pow(0.5)

    fun orientation(op: Int): Beacon {
        val result = mutableListOf(x, y, z)

        if (op and 0b000001 == 0) result[2] = result[2] * -1
        if (op and 0b000010 == 0) result[1] = result[1] * -1
        if (op and 0b000100 == 0) result[0] = result[0] * -1
        if (op and 0b001000 == 0) result.add(result.removeFirst())
        if (op and 0b010000 == 0) result.add(result.removeFirst())
        if (op and 0b100000 == 0) result.add(result.removeAt(1))

        return Beacon(result[0], result[1], result[2])
    }

    override fun toString(): String = "${x},${y},${z}"
}

fun Iterable<Double>.hasOverlappingDistancesWith(other: Iterable<Double>) =
    cartesianProduct(other).count { (x, y) -> abs(x - y) < 0.001 } >= 10

fun Iterable<Beacon>.align(other: Iterable<Beacon>): List<Pair<Beacon, Beacon>> = mapNotNull { beaconA ->
    other
        .firstOrNull { beaconB -> beaconA.distances.hasOverlappingDistancesWith(beaconB.distances) }
        .let { if (it == null) null else Pair(beaconA, it) }
}

class BeaconSet(val input: List<String>, val scanner: Scanner? = null) {
    val beacons: List<Beacon> = input.map {
        val (x, y, z) = it.split(",").map(String::toLong)
        Beacon(x + (scanner?.first ?: 0), y + (scanner?.second ?: 0), z + (scanner?.third ?: 0))
    }

    init {
        beacons.cartesianProduct(beacons).forEach { (a, b) -> a.neighbors.add(Pair(b, a.distanceTo(b))) }
    }

    fun overlappingBeacons(other: BeaconSet): List<Beacon> {
        val otherDistances = other.beacons.map { it.distances }.toSet()
        return beacons
            .filter { beacon -> otherDistances.any { distances -> beacon.distances.hasOverlappingDistancesWith(distances) } }
            .let { if (it.size >= 12) it else listOf() }
    }

    fun orientateToMap(map: Set<Beacon>): BeaconSet {
        val alignments = beacons.align(map)

        val (orientation, scanner) = (0..63).firstNotNullOf {
            val potential = alignments[0].first.orientation(it)
            val reference = alignments[0].second

            val scanner = Scanner(reference.x - potential.x, reference.y - potential.y, reference.z - potential.z)

            val check = alignments[1].first.orientation(it)
            val checkR = alignments[1].second

            if (scanner.first + check.x == checkR.x && scanner.second + check.y == checkR.y && scanner.third + check.z == checkR.z) {
                Pair(it, scanner)
            } else {
                null
            }
        }

        return BeaconSet(beacons.map { it.orientation(orientation).toString() }, scanner)
    }
}