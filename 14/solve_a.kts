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
val mapping = input.drop(1).map { it.split(" -> ").let { it[0] to it[1] }}.toMap()

fun apply(sequence: String): String {
  return sequence[0] + sequence.zipWithNext { a, b ->
    mapping["$a$b"]?.let {
      "$it$b"
    } ?: "$b"
  }.joinToString("")
}

println(apply(sequence))

val final = (0..9).fold(sequence) {sequence, _ -> apply(sequence) }

val groups = final.groupBy { it }.map { it.key to it.value.size }
val most = groups.maxByOrNull { it.second }
val least = groups.minByOrNull { it.second }

println(groups)
println(most!!.second - least!!.second)