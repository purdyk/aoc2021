#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

val sequence = input.first()
val mapping = input.drop(1).map { it.split(" -> ").let { it[0] to it[1][0] }}.toMap()

val cache = mutableMapOf<Pair<Pair<Char, Char>, Int>, Map<Char, Long>>()

fun MutableMap<Char,Long>.incrementBy(idx: Char, amt: Long) {
  this[idx] = this.getOrDefault(idx, 0L) + amt
}

fun MutableMap<Char,Long>.decrementBy(idx: Char, amt: Long) {
  this[idx] = this.getOrDefault(idx, 0L) - amt
}

val maxLayer = 40

fun countDownTo(pair: Pair<Char, Char>, layer: Int): Map<Char, Long>  {

  return cache.getOrPut(pair to (maxLayer - layer)) {
    val mid = mapping["${pair.first}${pair.second}"]?.takeIf { layer < maxLayer }
    mutableMapOf<Char, Long>().also { out ->
      if (mid != null) {
        countDownTo(pair.first to mid, layer + 1).forEach {
          out.incrementBy(it.key, it.value)
        }

        countDownTo(mid to pair.second, layer + 1).forEach {
          out.incrementBy(it.key, it.value)
        }

        out.decrementBy(mid, 1L)
      } else {
        out.incrementBy(pair.first, 1L)
        out.incrementBy(pair.second, 1L)
      }

    }
  }
}

val pairs = sequence.zipWithNext()

val before = Date()
val counts = pairs.map { countDownTo(it, 0) }.reduce { acc, map ->
  acc.toMutableMap().also {
    map.forEach { entry ->
      it.incrementBy(entry.key, entry.value)
    }
  }
}.toMutableMap()

sequence.dropLast(1).drop(1).forEach {
  counts.decrementBy(it, 1L)
}

val after = Date()

println(counts)

val most = counts.maxByOrNull { it.value }
val least = counts.minByOrNull { it.value }

println("B=2192039569602")
println(most)

println("H=3849876073")
println(least)

println(most!!.value - least!!.value)
println((after.time - before.time) / 1000.0)