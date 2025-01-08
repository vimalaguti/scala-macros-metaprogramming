package com.rockthejvm.macros_usage

import com.rockthejvm.macros.TreeMappings.*

object TreeMappings {
  val scopedValue = transformCode {
    def multiply(x: String, y: Int) = x * y
    val mol = 42
    val fl = "Scala"

    println(s"The meaning of life is $mol and fav language is $fl")
  }

  val flippedBooleans = flipBooleans {
    val x = true
    val y = 2 > 3
    val z = x && y
    def funcBool(a: Boolean, b: Boolean) = a && b

    if (z || false) funcBool(x, y)
    else false
  }

  val gatheredStatements = demoAccumulator {
    val x = 1 + 3

    println(x) // this should gather the expression x

    val y = {
      println("hello, I'm writing Scala")
      x * 3
    }

    def logNumbers(a: Int, b: Int) = {
      println(a)
      println(b)
      a + b
    }

    println(logNumbers(x, y))
  }
}
