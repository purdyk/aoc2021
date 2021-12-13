import java.io.File
import java.nio.charset.Charset
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }


var slots = mutableMapOf<Int, Long>()

val school = input[0].split(",").forEach {
  val slot = it.toInt()
  slots[slot] = slots.getOrDefault(slot, 0) + 1
}


(0 until 256).forEach {
  val newSlots = mutableMapOf<Int, Long>()

  (8 downTo 0).forEach {
    newSlots[it - 1] = slots.getOrDefault(it, 0L)
  }

  newSlots[6] = newSlots.getOrDefault(6, 0L) + newSlots.getOrDefault(-1, 0L)
  newSlots[8] = newSlots.getOrDefault(-1, 0L)

  slots = newSlots
}


println(slots.entries.filter { it.key > -1 }.sumOf { it.value })