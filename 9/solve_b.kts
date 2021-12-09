#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
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
    x - 1 to y,
    x + 1 to y,
    x to y - 1,
    x to y + 1
  )
}

fun get(coord: Pair<Int, Int>): Int? = input.getOrNull(coord.first)?.getOrNull(coord.second)

val lows = mutableListOf<Pair<Int, Int>>()

input.indices.forEach { x ->
  input[x].indices.forEach { y ->
    val current = input[x][y]
    val neighbors = neighborCoods(x to y).mapNotNull { get(it) }
    if (neighbors.all { current < it }) {
      lows.add(x to y)
    }
  }
}

println(lows.map { get(it) } )

fun makeBasin(low: Pair<Int, Int>): List<Pair<Int, Int>> {
  val curLow = get(low)!!
  val consider = neighborCoods(low).filter { get(it)?.let { it != 9 && it > curLow } == true }
  val basin = mutableListOf(low)

//  println("testing $low will consider: $consider")

  consider.forEach { possibleBasinContinuation ->
    val lowest = neighborCoods(possibleBasinContinuation).minByOrNull { get(it) ?: 99 }

    if (lowest == low) { // This will flow towards the previous basin point
//      println("$possibleBasinContinuation continues, diving in")
      basin.addAll(makeBasin(possibleBasinContinuation))
//    } else {
//      println("$possibleBasinContinuation does not continue")
    }
  }

  return basin
}

val basins = lows.map { makeBasin(it) }

val sizes = basins.sortedByDescending { it.size }.take(3).map { it.size }
println(sizes)

println(sizes.foldRight(1) { it, acc -> it*acc })