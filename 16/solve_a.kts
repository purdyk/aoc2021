#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }
  .first()

fun String.decodeHex(): String {
  check(length % 2 == 0) { "Must have an even length" }

  return chunked(2)
    .map { it.toInt(16).toString(2).padStart(8, '0') }.joinToString("")
}

class BitString(val wrapped: String) {
  var start = 0

  fun read(length: Int): String {
    val out = wrapped.substring(start, start + length)
    start += length
    return out
  }

  fun exhausted(): Boolean {
    return start > wrapped.lastIndex
  }
}


class Packet(data: BitString) {
  val version = data.read(3).toInt(2)
  val type = data.read(3).toInt(2)

  val sub: SubPacket

  fun versions(): List<Int> = listOf(version) + sub.versions()

  init {
    sub = when (type) {
      4 -> LiteralSub(data)

      else -> {
        val l = data.read(1)
        when (l) {
          "1" -> PacketLengthSub(data)
            else -> BitLengthSub(data)
        }
      }
    }
  }

  override fun toString(): String {
    return "P V:$version T:$type S: $sub"
  }
}

interface SubPacket {
  fun versions(): List<Int>
}

class LiteralSub(data: BitString) : SubPacket {
  val bytes: List<Int>

  override fun versions() = emptyList<Int>()

  init {
    val parsed = mutableListOf<Int>()
    do {
      val chunk = data.read(5)
      println("parsing $chunk")
      val keepNext = chunk.take(1) == "1"
      parsed.add(chunk.drop(1).toInt(2))
    } while (keepNext)

    bytes = parsed
  }
}

class PacketLengthSub(data: BitString): SubPacket {
  val length = data.read(11).toInt(2)
  val subs = (0 until length).map { Packet(data) }

  override fun versions(): List<Int> = subs.flatMap { it.versions() }
}

class BitLengthSub(data: BitString): SubPacket {
  val length = data.read(15).toInt(2)
  val subs: List<Packet>
  override fun versions(): List<Int> = subs.flatMap { it.versions() }

  init {
    val out = mutableListOf<Packet>()
    val subData = BitString(data.read(length))

    while (!subData.exhausted()) {
      out.add(Packet(subData))
    }

    subs = out
  }

}

println(input.decodeHex())
val root = Packet(BitString(input.decodeHex()))
println(root.versions())
println(root.versions().sum())