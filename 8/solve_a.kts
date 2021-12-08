#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.split(" | ").map { it.split(" ") } }

println(input)

val intMaps = mapOf(
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

val unique = input.flatMap { it[1].filter { it.length in intMaps.keys }}

println(unique.size)