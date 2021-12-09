#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.map { it.toString().toInt() }}

println(input)

fun neighbors(x: Int, y: Int): List<Int> {
  return listOfNotNull(
    input.getOrNull(x - 1)?.getOrNull(y),
    input.getOrNull(x + 1)?.getOrNull(y),
    input.getOrNull(x)?.getOrNull(y - 1),
    input.getOrNull(x)?.getOrNull(y + 1),
  )
}

val good = mutableListOf<Pair<Int, Int>>()

input.indices.forEach { x ->
  input[x].indices.forEach { y ->
    val current = input[x][y]
    val neighbors = neighbors(x, y)
    if (neighbors.all { current < it }) {
      good.add(x to y)
    }
  }
}

val risk = good.map { input[it.first][it.second] + 1 }

println(risk.sum())