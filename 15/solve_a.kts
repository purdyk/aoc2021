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

println(input)

fun get(loc: Pair<Int, Int>): Int?
  = input.getOrNull(loc.first)?.getOrNull(loc.second)

fun adjacent(pos: Pair<Int, Int>): List<Pair<Int, Int>>
  = listOf(
    pos.first to pos.second + 1,
//    pos.first to pos.second - 1,
    pos.first + 1 to pos.second,
//    pos.first - 1 to pos.second
  ).filter { get(it) != null }

val lastPoint = input.lastIndex to input[0].lastIndex

println(lastPoint)

val cache = mutableMapOf<Pair<Int, Int>, Int>() //Pair<Int, List<Pair<Int,Int>>>>()

cache[lastPoint] = 0 // to emptyList<Pair<Int, Int>>()

fun bestPath(previous: List<Pair<Int,Int>>): Int { //Pair<Int, List<Pair<Int,Int>>> {
  val thisLast = previous.last()

  return cache.getOrPut(thisLast) {

    val future = adjacent(previous.last())//.filterNot { it in previous }

    val best = future.map { next ->
      bestPath(previous + next).let { downstream ->
        get(next)!! + downstream // to listOf(next) + downstream.second
      }
    }.minOf { it }

    println("$thisLast: ${cache.size}")

    best
  }
}

val path = bestPath(listOf(0 to 0))

println(path)