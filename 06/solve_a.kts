import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

class Lanternfish(start: Int) {
  var current = start

  fun day(): Lanternfish? {
    current--
    if (current < 0) {
      current = 6
      return Lanternfish(8)
    }
    return null
  }
}

val school = input[0].split(",").map { Lanternfish(it.toInt())}.toMutableList()

(0 until 80).forEach {
  val newFish = school.mapNotNull { it.day() }
  school.addAll(newFish)
}

println(school.size)