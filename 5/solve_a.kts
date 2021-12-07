import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.split( " -> ").map { it.split(",").map { it.toInt() } } }

println(input[0])

val points = mutableMapOf<String, Int>()

input.forEach { (from, to) ->
  val xmax = Math.max(from[0], to[0])
  val xmin = Math.min(from[0], to[0])
  val ymax = Math.max(from[1], to[1])
  val ymin = Math.min(from[1], to[1])

  if (xmax == xmin) {
    (ymin..ymax).forEach {
      val key = "$xmax,$it"
      points[key] = points.getOrDefault(key, 0) + 1
    }
  } else if (ymax == ymin) {
    (xmin..xmax).forEach {
      val key = "$it,$ymax"
      points[key] = points.getOrDefault(key, 0) + 1
    }
  }
}

println(points.entries.filter { it.value > 1 }.count())