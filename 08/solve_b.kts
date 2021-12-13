#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.split(" | ").map { it.split(" ") } }

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

fun findA(scramble: List<String>): List<Pair<Char, Char>> {
  val seven = scramble.first { it.length == 3 }.toSet()
  val one = scramble.first { it.length == 2}.toSet()

  val a = (seven - one).first()

  return listOf(
    a to 'a'
  )
}

fun findCF(scramble: List<String>): List<Pair<Char, Char>> {
  val f609 = scramble.filter { it.length == 6 }.map { it.toSet() }
  val one = scramble.first { it.length == 2 }.toSet()

  val f = f609.map { one.intersect(it) }.first { it.size == 1 }.first()
  val c = (one - f).first()

  return listOf(
    f to 'f',
    c to 'c'
  )
}

// must be called after CF
fun findBE(scramble: List<String>, known: Map<Char, Char>): List<Pair<Char, Char>> {
  val f235 = scramble.filter { it.length == 5}.map { it.toSet() }

  val c = known.entries.first { it.value == 'c' }.key
  val f = known.entries.first { it.value == 'f' }.key

  val two = f235.first { it.contains(c) && !it.contains(f) }
  val five = f235.first { !it.contains(c) && it.contains(f) }

  val b = (five - two - f).first()
  val e = (two - five - c).first()

  return listOf(
    b to 'b',
    e to 'e'
  )
}

// must be called after BE
fun findDG(scramble: List<String>, known: Map<Char, Char>): List<Pair<Char, Char>> {
  val f609 = scramble.filter { it.length == 6 }.map { it.toSet() }

  val c = known.entries.first { it.value == 'c' }.key
  val e = known.entries.first { it.value == 'e' }.key

  val zero = f609.filter { it.contains(c) && it.contains(e) }.first()
  val nine = f609.filter { !it.contains(e) }.first()

  val d = (nine - zero).first()
  val g = (zero - known.keys - d).first()

  return listOf(
    d to 'd',
    g to 'g'
  )
}

fun infer(scramble: List<String>): Map<Char, Char> {
  val known = mutableMapOf<Char, Char>()

  known.putAll(findA(scramble))
  known.putAll(findCF(scramble))
  known.putAll(findBE(scramble, known))
  known.putAll(findDG(scramble, known))

  return known
}

val numbers = input.map {
  val mapping = infer(it[0])

  val fixed = it[1].map { it.map { mapping[it]!! }.sorted().joinToString("") }
  fixed.map { strMaps[it] }.joinToString("").toInt()
}

println(numbers.sum())
