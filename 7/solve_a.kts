#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .first().split(",").map { it.toInt() }

val min = input.minOf { it }
val max = input.maxOf { it }

val options = (min..max).map { cur ->
 input.map { Math.abs(it - cur) }.sumOf { it }.let { cur to it }
}

val winner = options.minByOrNull { it.second }!!

println(winner)
