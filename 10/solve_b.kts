#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

val delimeters = mapOf(
  '[' to ']',
  '{' to '}',
  '<' to '>',
  '(' to ')'
)

val points = mapOf(
  ')' to 1L,
  ']' to 2L,
  '}' to 3L,
  '>' to 4L
)

val opens = delimeters.keys

fun parseLine(line: String): List<Char>? {
  val stack = Stack<Char>()

  line.forEach {
    when (it) {
      in opens -> stack.push(it)
      else -> {
        if (delimeters[stack.pop()] != it) {
          return null
        }
      }
    }
  }

  return stack.reversed().map { delimeters[it]!! }
}

val errors = input.mapNotNull { parseLine(it) }

val values = errors.map { err ->
  err.fold(0L) { acc, it -> (acc * 5L) + points[it]!! }
}

println(values)

val sorted = values.sorted()

val mid = sorted.drop((sorted.size / 2)).first()

println(mid)