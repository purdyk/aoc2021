import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

val order = input[0].split(",").map { it.toInt() }

val boards = input.drop(1)
  .map { it.split(" ").filter { it.isNotEmpty() }.map { it.toInt() } }
  .chunked(5)
  .withIndex().associate { it.index to it.value }.toMutableMap()

fun boardHasRow(drawn: List<Int>, board: List<List<Int>>): Boolean {
  return board.any { row -> row.all { drawn.contains(it) } }
}

fun boardHasColumn(drawn: List<Int>, board: List<List<Int>>): Boolean {
  return board.first().indices.any { col -> board.all { drawn.contains(it[col]) } }
}

fun chooseWinningBoards(drawn: List<Int>): List<Int> {
  return boards.entries.filter {
    boardHasRow(drawn, it.value) || boardHasColumn(drawn, it.value)
  }.map { it.key }
}

for (idx in order.indices) {
  val drawn = order.subList(0, idx)
  val boardIds = chooseWinningBoards(drawn)

  boardIds.forEach { boardId ->
    val board = boards[boardId]!!
    boards.remove(boardId)
    val valid = board.flatMap { it }.filterNot { drawn.contains(it) }
    println(valid.sumOf { it } * drawn.last())
  }
}