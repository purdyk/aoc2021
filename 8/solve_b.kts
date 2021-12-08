#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.split(" | ").map { it.split(" ") } }

val uniqueMaps = mapOf(
  3 to 7,
  4 to 4,
  2 to 1,
  7 to 8
)

val strMaps = mapOf(
  "abcefg" to 0,
  "cf" to 1,
  "acdeg" to 2,
  "acdfg" to 3,
  "bcdf" to 4,
  "abdfg" to 5,
  "abdefg" to 6,
  "acf" to 7,
  "abcdefg" to 8,
  "abcdfg" to 9
)

val pairMaps = mapOf(
  (1 to 4) to ("" to "bd"),
  (1 to 7) to ("" to "a"),
  (1 to 8) to ("" to "abdeg"),
  (4 to 7) to ("bd" to "a"),
  (4 to 8) to ("" to "aeg"),
  (7 to 8) to ("" to "bdeg")
)

fun subtractGross(a: Set<Char>, b: Set<Char>, knownInA: String, known: Map<Char, Char>): Pair<Char, Char>? {
  if (knownInA.length == 0) return null

  // unique to a
  var unknownInA = a - b - known.keys

  // remove known keys from static
  val possible = knownInA.filterNot { known.values.contains(it) }

  if (possible.length == 1) {
    val found = unknownInA.first() to possible.first()
    return found
  }

  return null
}

fun findA(a: Pair<Int, String>, b: Pair<Int, String>, known: Map<Char, Char>): List<Pair<Char, Char>> {
  if (a.first == b.first) return emptyList()

  val sets = listOf(a, b).map { it.first to it.second.toSet() }

  val small = sets.minByOrNull { it.first }!!
  val big = sets.maxByOrNull { it.first }!!

  val deltas = pairMaps[small.first to big.first]!!

  return listOfNotNull(
    subtractGross(small.second, big.second, deltas.first, known),
    subtractGross(big.second, small.second, deltas.second, known)
  )
}

fun findCF(scramble: List<String>): List<Pair<Char, Char>> {
  val f609 = scramble.filter { it.length == 6 }.map { it.toSet() }
  val one = scramble.filter { it.length == 2 }.map { it.toSet() }.first()

  val f = f609.map { one.intersect(it) }.filter { it.size == 1 }.first()
  val c = one - f

  return listOf(
    f.first() to 'f',
    c.first() to 'c'
  )
}

// must be called after CF
fun findBE(scramble: List<String>, known: Map<Char, Char>): List<Pair<Char, Char>> {
  val f235 = scramble.filter { it.length == 5}.map { it.toSet() }

  val c = known.entries.first { it.value == 'c' }.key
  val f = known.entries.first { it.value == 'f' }.key

  val two = f235.filter { it.contains(c) && !it.contains(f) }.first()
  val five = f235.filter { !it.contains(c) && it.contains(f) }.first()

  val b = five - two - f
  val e = two - five - c

  return listOf(
    b.first() to 'b',
    e.first() to 'e'
  )
}

fun findDG(scramble: List<String>, known: Map<Char, Char>): List<Pair<Char, Char>> {
  val f609 = scramble.filter { it.length == 6 }.map { it.toSet() }

  val c = known.entries.first { it.value == 'c' }.key
  val e = known.entries.first { it.value == 'e' }.key

  val zero = f609.filter { it.contains(c) && it.contains(e) }.first()
  val nine = f609.filter { !it.contains(e) }.first()

  val d = nine - zero
  val g = zero - known.keys - d

  return listOf(
    d.first() to 'd',
    g.first() to 'g'
  )
}

fun infer(scramble: List<String>): Map<Char, Char> {
  val fixMap = mutableMapOf<Char, Char>()
  val uniques = scramble.mapNotNull { uniqueMaps[it.length]?.let { int -> int to it } }

  uniques.indices.forEach { a ->
    uniques.indices.filter { it > a }.forEach { b ->
      findA(uniques[a], uniques[b], fixMap).forEach { fixMap[it.first] = it.second }
    }
  }

  findCF(scramble).forEach { fixMap[it.first] = it.second }
  findBE(scramble, fixMap).forEach { fixMap[it.first] = it.second }
  findDG(scramble, fixMap).forEach { fixMap[it.first] = it.second }

  return fixMap
}

val numbers = input.map {
  val mapping = infer(it[0])

  val fixed = it[1].map { it.map { mapping[it]!! }.sorted().joinToString("") }
  fixed.map { strMaps[it] }.joinToString("").toInt()
}

println(numbers.sum())
