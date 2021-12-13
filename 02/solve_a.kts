import java.io.File
import java.nio.charset.Charset

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.split(" ") }
  .map { it[0] to it[1].toInt() }

println(input)

var depth = 0
var pos = 0

input.forEach { (cmd, value) ->
  when (cmd) {
    "up" -> depth -= value
    "down" -> depth += value
    "forward" -> pos += value
    else -> pos -= value
  }
}

println("depth: $depth pos: $pos tot: ${pos * depth}")