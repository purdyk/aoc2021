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

val wide = input.map { line ->
  (0..4).flatMap { width ->
    line.map { (it + width).let { if (it > 9) it - 9 else it } }
  }
}

val tall = (0..4).flatMap { height ->
  wide.map { line ->
    line.map { (it + height).let { if (it > 9) it - 9 else it } }
  }
}

//println(tall.joinToString("\n") { it.joinToString("") })

fun get(loc: Pair<Int, Int>): Int?
  = tall.getOrNull(loc.first)?.getOrNull(loc.second)

fun adjacent(pos: Pair<Int, Int>): List<Pair<Int, Int>>
  = listOf(
    pos.first to pos.second + 1,
    pos.first + 1 to pos.second,
  ).filter { get(it) != null }

val lastPoint = tall.lastIndex to tall[0].lastIndex

println(lastPoint)

val cache = mutableMapOf<Pair<Int, Int>, Int>() //Pair<Int, List<Pair<Int,Int>>>>()

cache[lastPoint] = 0 // to emptyList<Pair<Int, Int>>()

fun bestPath(previous: List<Pair<Int, Int>>): Int { //Pair<Int, List<Pair<Int,Int>>> {
  val thisLast = previous.last()

  return cache.getOrPut(thisLast) {
    adjacent(thisLast).map { next ->
      bestPath(previous + next).let { downstream ->
        get(next)!! + downstream // to listOf(next) + downstream.second
      }
    }.minOf { it }
  }
}

val path = bestPath(listOf(0 to 0))

//println((0..tall.lastIndex).joinToString("\n") { x ->
//  (0..tall.lastIndex).joinToString("") { y -> cache[x to y].toString() }
//})

println(path)