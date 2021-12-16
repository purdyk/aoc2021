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

interface Packet {
  fun versions(): List<Int>
  fun value(): Long
}

class BasePacket(data: BitString): Packet {
  val version = data.read(3).toInt(2)
  val type = data.read(3).toInt(2)

  val sub: Packet

  override fun versions(): List<Int> = listOf(version) + sub.versions()
  override fun value() = sub.value()

  init {
    sub = when (type) {
      4 -> LiteralSub(data)

      else -> {
        val l = data.read(1)
        when (l) {
          "1" -> PacketLengthSub(type, data)
            else -> BitLengthSub(type, data)
        }
      }
    }
  }

  //  override fun toString(): String {
//    return "P V:$version T:$type S: $sub"
//  }
  override fun toString() = sub.toString()
}

class LiteralSub(data: BitString) : Packet {
  val bytes: List<Int>

  override fun versions() = emptyList<Int>()

  override fun value(): Long = bytes.fold(0L) { acc, i ->
    acc.shl(4) + i
  }

  override fun toString() = "${value()}"

  init {
    val parsed = mutableListOf<Int>()
    do {
      val chunk = data.read(5)
//      println("parsing $chunk")
      val keepNext = chunk.take(1) == "1"
      parsed.add(chunk.drop(1).toInt(2))
    } while (keepNext)

    bytes = parsed

//    println("Literal parsed: $bytes ${value()}")
  }
}

abstract class OpPacket(val type: Int) : Packet {

  abstract val subs: List<Packet>

  override fun value(): Long {
    return when (type) {
      0 -> sum()
      1 -> product()
      2 -> min()
      3 -> max()
      5 -> gt()
      6 -> lt()
      else -> eq()
    }
  }

  fun sum() = subs.map { it.value() }.sum()
  fun product() = subs.fold(1L) { acc, packet -> acc * packet.value() }
  fun min() = subs.minOf { it.value() }
  fun max() = subs.maxOf { it.value() }

  fun gt(): Long {
    val test = subs.first().value()
    return if (subs.drop(1).all { it.value() < test }) 1 else 0
  }

  fun lt(): Long {
    val test = subs.first().value()
    return if (subs.drop(1).all { it.value() > test }) 1 else 0
  }

  fun eq(): Long {
    val test = subs.first().value()
    return if (subs.drop(1).all { it.value() == test }) 1 else 0
  }

  val opName get() = when(type) {
    0 -> "sum"
    1 -> "prod"
    2 -> "min"
    3 -> "max"
    5 -> "gt"
    6 -> "lt"
    else -> "eq"
  }

  override fun toString() = "$opName(${subs.joinToString()})"
}

class PacketLengthSub(type: Int, data: BitString): OpPacket(type) {
  val length = data.read(11).toInt(2)
  override val subs: List<Packet> = (0 until length).map { BasePacket(data) }

  override fun versions(): List<Int> = subs.flatMap { it.versions() }
}

class BitLengthSub(type: Int, data: BitString): OpPacket(type) {
  val length = data.read(15).toInt(2)
  override val subs: List<Packet>
  override fun versions(): List<Int> = subs.flatMap { it.versions() }

  init {
    val out = mutableListOf<BasePacket>()
    val subData = BitString(data.read(length))

    while (!subData.exhausted()) {
      out.add(BasePacket(subData))
    }

    subs = out
  }

}

println(input.decodeHex())
val root = BasePacket(BitString(input.decodeHex()))
//println(root.versions())
println(root.versions().sum())
println(root.value())
println(root)