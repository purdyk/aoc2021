#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.map { it.toString().toInt() }}

fun neighborCoods(coord: Pair<Int, Int>): List<Pair<Int, Int>> {
  val x = coord.first
  val y = coord.second

  return listOf(
    x to y - 1,
    x to y + 1,
    x - 1 to y,
    x - 1 to y - 1,
    x - 1 to y + 1,
    x + 1 to y,
    x + 1 to y - 1,
    x + 1 to y + 1
  )
}

fun flash(grid: List<List<Int>>): Pair<Int, List<List<Int>>> {
  val newGrid = grid.map { it.toMutableList() }
  val didFlash = mutableListOf<Pair<Int, Int>>()

  newGrid.forEachIndexed { xidx, x ->
    x.forEachIndexed {  yidx, y ->
      newGrid[xidx][yidx] = y + 1
    }
  }

  do {
    val flashers =
      newGrid.indices.flatMap { x -> newGrid[0].indices.map { y -> x to y } }
        .filter { newGrid[it.first][it.second] > 9 && !didFlash.contains(it) }

    flashers.forEach { flasher ->
      neighborCoods(flasher).forEach { neighbor ->
        newGrid.getOrNull(neighbor.first)?.getOrNull(neighbor.second)?.also {
          newGrid[neighbor.first][neighbor.second] = it + 1
        }
      }
    }

    didFlash.addAll(flashers)
  } while (flashers.size > 0)

  didFlash.forEach {
    newGrid[it.first][it.second] = 0
  }

  return didFlash.size to newGrid
}

var grid = input
var totalFlashed = 0

(0 until 100).forEach {
  val (flashed, newGrid) = flash(grid)
  grid = newGrid
  totalFlashed += flashed
//  println(grid.joinToString("\n") { it.joinToString(".")})
//  println(flashed)
}

println(totalFlashed)
