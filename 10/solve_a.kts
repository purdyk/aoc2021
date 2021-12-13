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
  ')' to 3,
  ']' to 57,
  '}' to 1197,
  '>' to 25137
)

val opens = delimeters.keys

fun parseLine(line: String): Char? {
  val stack = Stack<Char>()

  line.forEach {
    when (it) {
      in opens -> stack.push(it)
      else -> {
        if (delimeters[stack.pop()] != it) {
          return it
        }
      }
    }
  }
  return null
}

val errors = input.mapNotNull { parseLine(it) }

println(errors)

val values = errors.map { points[it]!!.toLong() }

println(values.sum())

//println(values.fold(1L) { acc, it -> acc * it })