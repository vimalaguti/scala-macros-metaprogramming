package com.rockthejvm.playground

object Playground {
  private transparent inline def compute(x: Int): Int = x + 2
  val meaningOfLife: 42 = compute(40)

  def main(args: Array[String]): Unit = {
    println(s"Rock and roll! The meaning of life is $meaningOfLife")
  }
}
