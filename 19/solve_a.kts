#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import kotlin.math.absoluteValue

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n\n")
  .map { it.split("\n").drop(1).filter { it.isNotEmpty() } }

inline fun doLog(g: () -> Any?) { if (true) { println(g()) } }

enum class Axis { X, Y, Z }

data class Beacon(val x: Int, val y: Int, val z:Int) {
  override fun toString(): String {
    return "[$x, $y, $z]"
  }

  fun rotated(matrix: List<List<Int>>): Beacon {
    return matrix.map { (x * it[0] + y * it[1] + z * it[2]).toInt() }
      .let { (nx, ny, nz) -> Beacon(nx, ny, nz) }
  }

  fun vectorTo(other: Beacon): List<Int> {
    return listOf<(Beacon) -> Int>({it.x}, {it.y}, {it.z})
      .map { it(this) - it(other) }
  }

  fun normalizedVectorTo(other: Beacon): List<Int> {
    val vec = vectorTo(other)
    val fac = vec.maxOf { it.absoluteValue }
    return if (fac == 0) vec else vec.map { it / fac }
  }

  fun distanceTo(other: Beacon): Double {
    return Math.sqrt(
      vectorTo(other).map { Math.pow(it.toDouble(), 2.0) }.sum()
    ).absoluteValue
  }

  fun offset(vec: List<Int>): Beacon {
    return Beacon(x + vec[0], y + vec[1], z + vec[2])
  }
}

class Scanner(val beacons: List<Beacon>) {
  override fun toString(): String {
    return "Scanner(${beacons.joinToString(",")})"
  }

  fun offset(vector: List<Int>): Scanner {
    return Scanner(beacons.map { it.offset(vector) })
  }

  fun rotated(pair: Pair<Axis, Int>): Scanner {
    return rotated(pair.first, pair.second)
  }

  fun rotated(axis: Axis, angles: Int): Scanner {
    val deg = Math.toRadians(90.0 * angles)

    val matrix = when (axis) {
      Axis.X -> listOf(
        listOf(1.0, 0.0, 0.0),
        listOf(0.0, Math.cos(deg), -Math.sin(deg)),
        listOf(0.0, Math.sin(deg), Math.cos(deg))
      )

      Axis.Y -> listOf(
        listOf(Math.cos(deg), 0.0, Math.sin(deg)),
        listOf(0.0, 1.0, 0.0),
        listOf(-Math.sin(deg), 0.0, Math.cos(deg))
      )

      Axis.Z -> listOf(
        listOf(Math.cos(deg), -Math.sin(deg), 0.0),
        listOf(Math.sin(deg), Math.cos(deg), 0.0),
        listOf(0.0, 0.0,  1.0)
      )
    }.map { it.map { it.toInt() }}

    return Scanner(beacons.map { it.rotated(matrix) })
  }

  fun distancesFrom(beacon: Beacon): List<Pair<Beacon, Double>> {
    return beacons.mapNotNull { (it to beacon.distanceTo(it)).takeIf { it.second > 0 } }
  }

  fun overlapping(other: Scanner): List<Pair<Beacon, Beacon>> {
    val alingment = beacons.mapNotNull { a ->
      val toOthers = distancesFrom(a)
      other.beacons.filter { b ->
        // N other equidistant points probably means they're the same
        other.distancesFrom(b).filter { t -> toOthers.any { it.second == t.second } }.size > 10
      }.firstOrNull()?.let { a to it }
    }

    return alingment
  }

  fun aligned(other: Scanner): Boolean {
    val overlap = overlapping(other)

    return overlap.indices.drop(1).all {
      overlap[0].first.normalizedVectorTo(overlap[it].first) ==
          overlap[0].second.normalizedVectorTo(overlap[it].second)
    }
  }

  companion object {
    val rotations =
      listOf(
        Axis.X to 0,
        Axis.Y to 1,
        Axis.Y to 3,
        Axis.Z to 1,
        Axis.Z to 3,
        Axis.Z to 2
      ).flatMap { a ->
        (0..3).map {
          a to (Axis.X to it)
        }
      }
  }
}

data class Orientation(val rotA: Pair<Axis, Int>, val rotB: Pair<Axis, Int>, val offset: List<Int>)

val scanners = input.map { s ->
  Scanner(s.map { it.split(",").map { it.toInt() }.let { (x, y, z) -> Beacon(x, y, z) } })
}

//doLog { scanners.joinToString("\n") }

val neighbors = mutableListOf<Pair<Int, Int>>()

scanners.indices.forEach { a ->
  scanners.indices.forEach { b ->
    if (a < b) {
      if (scanners[a].overlapping(scanners[b]).size >= 12) {
        doLog { "$a aligns to $b" }
        neighbors.add(a to b)
      }
    }
  }
}

val unoriented = scanners.indices.drop(1).toMutableList()
val reoriented = mutableMapOf(0 to Orientation(Axis.X to 0, Axis.X to 0, listOf(0,0,0)))

while (unoriented.size > 0) {
  val done = reoriented.keys
  val toOrient = unoriented.firstNotNullOf { tryNeighbor ->
    val myNeighbors =
      (neighbors.filter { it.first == tryNeighbor }.map { it.second}
          + neighbors.filter { it.second == tryNeighbor }.map { it.first })
    myNeighbors.firstOrNull { it in done }?.let { tryNeighbor to it }
  }

  doLog { "Aligning ${toOrient.first} to ${toOrient.second}" }

  val good = reoriented[toOrient.second]!!.let { scanners[toOrient.second].rotated(it.rotA).rotated(it.rotB) }

  val (rotated, rotation) = Scanner.rotations.firstNotNullOf { rots ->
    scanners[toOrient.first].rotated(rots.first).rotated(rots.second)
      .takeIf { it.aligned(good) }?.let { it to rots }
  }

  doLog { "Rotated: $rotation" }

  // Find the overlapping points again
  val overlap = good.overlapping(rotated)
  val rawOffset = overlap[0].first.vectorTo(overlap[0].second)
  val offset = rawOffset.zip(reoriented[toOrient.second]!!.offset).map { (a,b) -> a+b }

  doLog { "Offset: $offset" }

  reoriented[toOrient.first] =
    Orientation(rotation.first, rotation.second, offset)

  unoriented.remove(toOrient.first)
}

val totalBeacons = mutableSetOf<Beacon>()

reoriented.forEach { idx, orientation ->
  val remapped =
    scanners[idx]
      .rotated(orientation.rotA)
      .rotated(orientation.rotB)
      .offset(orientation.offset)

  totalBeacons.addAll(remapped.beacons)
}

println(totalBeacons.size)