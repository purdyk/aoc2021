import java.io.File
import java.nio.charset.Charset

val input = File("./1/input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .map { it.toInt() }

val i0 = input.iterator()
val i1 = input.iterator()
val i2 = input.iterator()

i1.next()
i2.next()
i2.next()

val triples = mutableListOf<List<Int>>()

while(i2.hasNext()) {
  triples.add(listOf(i0.next(), i1.next(), i2.next()))
}

val sums = triples.map { it.sum() }

val output = sums.zipWithNext().filter { it.first < it.second }

println(output.size)