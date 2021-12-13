import java.io.File
import java.nio.charset.Charset

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

val cols = input.first().indices.map { idx -> input.map { it[idx] } }
val counts = cols.map { it.groupingBy { it }.eachCount() }

val gammaS = counts.map { it.maxByOrNull { it.value }!!.key }.joinToString("")
val epsilonS = counts.map { it.minByOrNull { it.value }!!.key }.joinToString("")

val gamma = Integer.parseInt(gammaS, 2)
val epsilon = Integer.parseInt(epsilonS, 2)

println(gamma)
println(epsilon)
println(gamma * epsilon)

