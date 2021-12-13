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

val costs = mutableMapOf<Int, Int>()

fun cost(distance: Int): Int {
  return costs.getOrPut(distance) {
    if (distance > 1) {
       distance + cost(distance - 1)
    } else {
     distance
    }
  }
}

val options = (min..max).map { cur ->
 input.map { cost(Math.abs(it - cur)) }.sumOf { it }.let { cur to it }
}

val winner = options.minByOrNull { it.second }!!

println(winner)

