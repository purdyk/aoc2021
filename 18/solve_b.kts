#!/usr/bin/env kotlin 
import java.io.File
import java.nio.charset.Charset
import java.util.*

val input = File("./input")
  .readText(Charset.forName("UTF8"))
  .split("\n")
  .filter { it.isNotBlank() }

//fun doLog(st: String) {
//  if (doLog) {
//    println(st)
//  }
//}

open class SnailNumber() {
  var parent: WrapperSnail? = null
  open fun reduce(depth: Int, split: Boolean): Boolean { return false }
  open fun upRight(from: SnailNumber): ConcreteSnail? { return null }
  open fun upLeft(from: SnailNumber): ConcreteSnail? { return null }
  open fun magnitude(): Int { return 0 }
  open fun copy(): SnailNumber { return this } // lol
}

class WrapperSnail(var left: SnailNumber, var right: SnailNumber): SnailNumber() {

  override fun reduce(depth: Int, split: Boolean): Boolean {
//    if (depth == 0) {
//      doLog("reduce: $this")
//    }

    if (depth >= 4 && left is ConcreteSnail && right is ConcreteSnail) {
      explode()
      return true
    }

    else if (left.reduce(depth + 1, split)) {
      return true
    }

    else if (right.reduce(depth + 1, split)) {
      return true
    }

    return false
  }

  private fun explode() {
//    doLog("exploding ${toString()}")

    parent!!.upLeft(this)?.also {
//      doLog("explode add left: $it")
      it.number += (left as ConcreteSnail).number
    }

    parent!!.upRight(this)?.also {
//      doLog("explode add right: $it")
      it.number += (right as ConcreteSnail).number
    }

    parent!!.replace(this, ConcreteSnail(0))
  }

  fun replace(from: SnailNumber, new: SnailNumber) {
    new.parent = this

    if (from == left) {
//      doLog("replace $left with $new")
      left = new
    } else if (from == right) {
//      doLog("replace $right with $new")
      right = new
    } else {
//      doLog("cannot replace")
    }
  }

  override fun upRight(from: SnailNumber): ConcreteSnail? {
    if (from == right) {
      return parent?.upRight(this)
    } else {
      return (right as? ConcreteSnail) ?: (right as WrapperSnail).downLeft()
    }
  }

  override fun upLeft(from: SnailNumber): ConcreteSnail? {
    if (from == left) {
      return parent?.upLeft(this)
    } else {
      return (left as? ConcreteSnail) ?: (left as WrapperSnail).downRight()
    }
  }

  fun downLeft(): ConcreteSnail {
    return (left as? ConcreteSnail) ?: (left as WrapperSnail).downLeft()
  }

  fun downRight(): ConcreteSnail {
    return (right as? ConcreteSnail) ?: (right as WrapperSnail).downRight()
  }

  override fun magnitude(): Int {
    return left.magnitude() * 3 + right.magnitude() * 2
  }

  override fun copy(): SnailNumber {
    val nL = left.copy()
    val nR = right.copy()
    val nP = WrapperSnail(nL, nR)
    nL.parent = nP
    nR.parent = nP
    return nP
  }

  override fun toString(): String {
    return "[$left, $right]"
  }
}

class ConcreteSnail(var number: Int): SnailNumber() {
  override fun reduce(depth: Int, split: Boolean): Boolean {
    if (split && number > 9) {
//      doLog("Splitting $number")
      val lN = ConcreteSnail(Math.floor(number / 2.0).toInt())
      val rN = ConcreteSnail(Math.ceil(number / 2.0).toInt())
      val p = WrapperSnail(lN, rN)
      lN.parent = p
      rN.parent = p
      parent!!.replace(this, p)
      return true
    }

    return false
  }

  override fun upRight(from: SnailNumber): ConcreteSnail? {
    return this
  }

  override fun upLeft(from: SnailNumber): ConcreteSnail? {
    return this
  }

  override fun toString(): String {
    return number.toString()
  }

  override fun magnitude(): Int {
    return number
  }

  override fun copy(): SnailNumber {
    return ConcreteSnail(number)
  }
}

class EmptySnail(): SnailNumber()

operator fun SnailNumber.plus(right: SnailNumber): SnailNumber {
  val wrap = WrapperSnail(this, right)

  this.parent = wrap
  right.parent = wrap

  do {
    val did = wrap.reduce(0, false) || wrap.reduce(0, true)
  } while (did)

  return wrap
}

fun parse(value: String): SnailNumber {
  val stack = Stack<SnailNumber>()
  val reader = value.iterator()

  while (reader.hasNext()) {
    val n = reader.next()

    when {
      n.isDigit() -> { stack.push(ConcreteSnail(n.toString().toInt())) }
      n == ']' -> {
        val right = stack.pop()
        val left = stack.pop()
        val parent = WrapperSnail(left, right)
        stack.push(parent)
        left.parent = parent
        right.parent = parent
      }
      else -> {}
    }
  }

  return stack.pop()
}

val nums = input.map { parse(it) }

val sums = nums.indices.flatMap { x ->
  nums.indices.mapNotNull { y ->
    if (x == y) { null } else {
        val xn = nums[x].copy()
        val yn = nums[y].copy()

      (xn to yn) to (xn + yn).magnitude()
    }
  }
}

println(sums.maxByOrNull { it.second })