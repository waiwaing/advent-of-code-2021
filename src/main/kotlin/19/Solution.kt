package `19`

import `15`.cartesianProduct
import kotlin.math.abs
import kotlin.math.pow

fun run_a(input: List<String>) = buildMasterBeaconMap(input).first.size.toString()

fun run_b(input: List<String>) = buildMasterBeaconMap(input).second.let {
    it.cartesianProduct(it)
        .maxOf { (a, b) -> abs(a.first - b.first) + abs(a.second - b.second) + abs(a.third - b.third) }
        .toString()
}

fun buildMasterBeaconMap(input: List<String>): Pair<Set<Beacon>, Set<Scanner>> {
    val masterBeaconMap = mutableSetOf<Beacon>()
    val scanners = mutableSetOf<Scanner>()

    val unincludedBeaconSets = input.joinToString("*").split("**")
        .map { it.split("*").filterNot { line -> line.startsWith("---") } }
        .map(::BeaconSet).toMutableSet()
    val includedBeaconSets = mutableListOf(unincludedBeaconSets.first())

    masterBeaconMap.addAll(unincludedBeaconSets.first().beacons)
    while (includedBeaconSets.isNotEmpty()) {
        unincludedBeaconSets.filter { includedBeaconSets.first().overlappingBeacons(it).isNotEmpty() }
            .onEach {
                val orientedBeaconSet = it.orientToMap(masterBeaconMap)
                masterBeaconMap.addAll(orientedBeaconSet.beacons)
                orientedBeaconSet.scanner?.let { scanner -> scanners.add(scanner) }

                unincludedBeaconSets.remove(it)
                includedBeaconSets.add(it)
            }.let { if (it.isEmpty()) includedBeaconSets.removeFirst() }
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

fun Iterable<Double>.overlaps(b: Iterable<Double>) = cartesianProduct(b).count { (x, y) -> abs(x - y) < 0.001 } >= 10

fun Sequence<Beacon>.align(b: Iterable<Beacon>) = mapNotNull { beaconA ->
    b.firstOrNull { beaconB -> beaconA.distances.overlaps(beaconB.distances) }
        .let { if (it == null) null else Pair(beaconA, it) }
}

class BeaconSet(val input: List<String>, val scanner: Scanner? = null) {
    val beacons: List<Beacon> = input.map {
        val (x, y, z) = it.split(",").map(String::toLong)
        Beacon(x + (scanner?.first ?: 0), y + (scanner?.second ?: 0), z + (scanner?.third ?: 0))
    }
    private val distancesSet by lazy { beacons.map { it.distances }.toSet() }

    init {
        beacons.cartesianProduct(beacons).forEach { (a, b) -> a.neighbors.add(Pair(b, a.distanceTo(b))) }
    }

    fun overlappingBeacons(other: BeaconSet) = beacons
        .filter { beacon -> other.distancesSet.any { distances -> beacon.distances.overlaps(distances) } }
        .let { if (it.size >= 12) it else listOf() }

    fun orientToMap(map: Set<Beacon>): BeaconSet {
        val alignments = beacons.asSequence().align(map).take(2).toList()

        val (orientation, scanner) = (0b000000..0b111111).firstNotNullOf {
            val (newA, refA) = Pair(alignments[0].first.orientation(it), alignments[0].second)
            val (newB, refB) = Pair(alignments[1].first.orientation(it), alignments[1].second)
            val scannerA = Scanner(refA.x - newA.x, refA.y - newA.y, refA.z - newA.z)
            val scannerB = Scanner(refB.x - newB.x, refB.y - newB.y, refB.z - newB.z)

            if (scannerA == scannerB) Pair(it, scannerA) else null
        }

        return BeaconSet(beacons.map { it.orientation(orientation).toString() }, scanner)
    }
}