import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

val order = input[0].split(",").map { it.toInt() }
val boards = input.drop(1).chunked(5).map {
  it.map { it.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }
}

fun boardHasRow(drawn: List<Int>, board: List<List<Int>>): Boolean {
  return board.any { row -> row.all { drawn.contains(it) } }
}

fun boardHasColumn(drawn: List<Int>, board: List<List<Int>>): Boolean {
  return board.first().indices.any { col -> board.all { drawn.contains(it[col]) } }
}

fun chooseWinningBoard(drawn: List<Int>): List<List<Int>>? {
  return boards.firstOrNull {
    boardHasRow(drawn, it) || boardHasColumn(drawn, it)
  }
}

for (idx in order.indices) {
  val drawn = order.subList(0, idx)
  val board = chooseWinningBoard(drawn)

  if (board != null) {
    println(idx)

    val valid = board.flatMap { it }.filterNot { drawn.contains(it) }

    println(valid)

    println(valid.sumOf { it } * drawn.last())

    break
  }
}