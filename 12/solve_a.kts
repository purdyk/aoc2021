#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map {
    it.split("-").let { it[0] to it[1] }
  }

val graph = mutableMapOf<String, MutableSet<String>>()

input.forEach {
  graph.getOrPut(it.first, { mutableSetOf<String>() }).add(it.second)
  graph.getOrPut(it.second, { mutableSetOf<String>() }).add(it.first)
}

fun permutate(history: List<String>): List<List<String>> {
  val node = history.last()

  if (node == "end") {
    return listOf(history)
  }

  val next = graph[node]?.filterNot { it == "0" || (it[0].isLowerCase() && it in history) }

//  println("on $node considering $next")

  return next?.flatMap {
    permutate(history + it)
  } ?: emptyList()

}

println(permutate(listOf("start")).size)