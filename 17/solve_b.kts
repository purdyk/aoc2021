#!/usr/bin/env kotlin 
import java.awt.Point
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern
import kotlin.math.sign

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .first()
  .split("target area: ")[1].split(", ").map { it.split("=") }

val xRange = input.first { it[0] == "x"}.let { it[1].split("..").map{ it.toInt() }.let { IntRange(it[0], it[1])}}
val yRange = input.first { it[0] == "y"}.let { it[1].split("..").map{ it.toInt() }.let { IntRange(it[0], it[1])}}

println("xr: $xRange yr: $yRange")

fun simulate(velocity: Point): Sequence<Point> =
  sequence {
    var pos = Point(0, 0)
    while (pos.x < xRange.last && pos.y > yRange.first) {
      pos.x += velocity.x
      pos.y += velocity.y
      velocity.x += -(velocity.x.sign)
      velocity.y -= 1
      yield(Point(pos))
      if (pos.x in xRange && pos.y in yRange)
        break
    }
  }

val possibleX = 1..xRange.last
val possibleY = yRange.first..(-yRange.first)

val winners = possibleX.flatMap { curX ->
  possibleY.mapNotNull { curY ->
    Point(curX, curY).takeIf {
      simulate(it).last().let { it.x in xRange && it.y in yRange }
    }
  }
}

println(winners.size)