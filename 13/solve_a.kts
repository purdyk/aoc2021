#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .partition { it[0] != 'f' }

val dots = input.first.map { it.split(",").map { it.toInt() }.let { it[0] to it[1]} }.toSet()
val folds = input.second.map { it.split("fold along ")[1].split("=") }


fun fold(along: List<String>, dots: Set<Pair<Int,Int>>): Set<Pair<Int,Int>> {
  return when (along[0]) {
    "x" -> foldAlongX(along[1].toInt(), dots)
    else -> foldAlongY(along[1].toInt(), dots)
  }
}

fun foldAlongY(line: Int, dots: Set<Pair<Int,Int>>): Set<Pair<Int,Int>> {
  return dots.mapNotNull {
    when {
      it.second < line -> it
      it.second == line -> null
      else -> it.first to line - (it.second - line)
    }
  }.toSet()
}

fun foldAlongX(line: Int, dots: Set<Pair<Int,Int>>): Set<Pair<Int,Int>> {
  return dots.mapNotNull {
    when {
      it.first < line -> it
      it.first == line -> null
      else ->  line - (it.first - line) to it.second
    }
  }.toSet()
}

fun printGrid(dots: Set<Pair<Int,Int>>) {
  val maxX = dots.maxOf { it.first }
  val maxY = dots.maxOf { it.second }

  val grid = (0..maxY).map { y ->
    (0..maxX).joinToString("") { x -> if (dots.contains(x to y)) "#" else "." }
  }.joinToString("\n")

  println(grid)
  println(dots.size)
}

printGrid(dots)

val final = fold(folds[0], dots)

printGrid(final)
