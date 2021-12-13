#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.split("-").let { it[0] to it[1] } }

val graph = mutableMapOf<String, MutableSet<String>>()

input.forEach {
  graph.getOrPut(it.first, { mutableSetOf<String>() }).add(it.second)
  graph.getOrPut(it.second, { mutableSetOf<String>() }).add(it.first)
}

fun permutate(history: List<String>, selected: String?): List<List<String>> {
  val node = history.last()

  if (node == "end") {
    return listOf(history)
  }

  val next = graph[node]?.filterNot { it == "start" || (it[0].isLowerCase() && it in history) }

//  println("on $node considering $next")

  var tail = next?.flatMap {
    permutate(history + it, selected)
  } ?: emptyList()

  if (selected == null) {
    val revisits = graph[node]?.filter { it != "start" && it[0].isLowerCase() && history.filter { h -> h == it }.size == 1 }

//    println("history: $history, will revisit: $revisits")

    tail += revisits?.flatMap {
      permutate(history + it, it)
    } ?: emptyList()
  }

  return tail
}

println(permutate(listOf("start"), null).size)