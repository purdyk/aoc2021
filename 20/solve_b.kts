#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n\n")

inline fun doLog(g: () -> Any?) { if (true) { println(g()) } }

val codec = input[0].split("\n").joinToString("")
val startImage = input[1].split("\n").filter { it.isNotEmpty() }

fun splay(pixel: Pair<Int, Int>) =
  (pixel.first - 1..pixel.first + 1).flatMap { x ->
    (pixel.second - 1..pixel.second + 1).map { y -> x to y }
  }

fun Char.iS() = if (this == '#') "1" else "0"

val even = codec[0].iS()
val odd = codec[(0..8).map { even }
  .joinToString("").toInt(2)].iS()

println("e: $even o: $odd")

fun index(pixels: Set<Pair<Int, Int>>, pixel: Pair<Int, Int>, xRange: IntRange, yRange: IntRange, iteration: Int) =
  splay(pixel)
    .map {
      if (it in pixels) '1' else {
        when {
          it.first in xRange && it.second in yRange -> '0'
          iteration.mod(2) == 0 -> even
          else -> odd
        }
      }
    }
    .joinToString("")
    .toInt(2)

fun ranges(pixels: Set<Pair<Int, Int>>): Pair<IntRange, IntRange> {
  val minX = pixels.minOf { it.first }
  val maxX = pixels.maxOf { it.first }
  val minY = pixels.minOf { it.second }
  val maxY = pixels.maxOf { it.second }

  return minX..maxX to minY..maxY
}

fun IntRange.pad(amt: Int) = this.first - amt .. this.last + amt

fun enhance(pixels: Set<Pair<Int, Int>>, iteration: Int): Set<Pair<Int,Int>> {
  val out = mutableSetOf<Pair<Int, Int>>()
  val (xRange, yRange) = ranges(pixels)

  xRange.pad(5).forEach { x ->
    yRange.pad(5).forEach { y ->
      val pixel = x to y
      if (codec[index(pixels, pixel, xRange, yRange, iteration)] == '#') {
        out.add(pixel)
      }
    }
  }

  return out
}

fun printPhoto(pixels: Set<Pair<Int, Int>>) {
  val (xRange, yRange) = ranges(pixels)
  val photo = xRange.pad(2).joinToString("\n") { x ->
    yRange.pad(2).joinToString("") { y -> if (x to y in pixels) "#" else "." }
  }

  println(photo)
}

val pixels = startImage.indices.flatMap { x ->
  startImage[x].indices.mapNotNull { y ->
    (x to y).takeIf { startImage[x][y] == '#' }
  }
}.toSet()

val final = (1..50).fold(pixels) { pixels, iteration -> enhance(pixels, iteration) }

println(final.size)