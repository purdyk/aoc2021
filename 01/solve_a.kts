import java.io.File
import java.nio.charset.Charset

val input = File("./1/input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.toInt() }

val output = input.zipWithNext().filter { it.first < it.second }

println(output.size)