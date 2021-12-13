import java.io.File
import java.nio.charset.Charset

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

val indices = input.first().indices

fun maxOf(i: List<String>, col: Int): Char {
  val cols = indices.map { idx -> i.map { it[idx] }}
  val count = cols[col].groupingBy { it }.eachCount()
  return count.let {
    val x = it.maxByOrNull { it.value }
    val n = it.minByOrNull { it.value }
    if (x?.value == n?.value || x?.key == n?.key) '1' else x!!.key
  }
}

var o2 = input
var col = 0

while (o2.size > 1) {
  val max = maxOf(o2, col)
  o2 = o2.filter { it[col] == max }
  col += 1
}

var co2 = input
col = 0

while (co2.size > 1) {
  val max = maxOf(co2, col)
  co2 = co2.filter { it[col] != max }
  col += 1
}

println(o2)
println(co2)

println(Integer.parseInt(o2.first(), 2) * Integer.parseInt(co2.first(), 2))