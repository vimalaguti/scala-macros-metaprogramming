package com.rockthejvm.inlines

import compiletime.* 
import com.rockthejvm.inlines.tools.*


object TupleMatching {

  // given any concrete type T, we're going to automatically print that kind of value
  inline def showTuple[T <: Tuple](tuple: T): String = 
    inline tuple match {
      case EmptyTuple => ""
      // (1, "a", true) == 1 *: "a" *: true
      case tup: (h *: t) =>
        val h *: t = tup
        summonInline[Show[h]].show(h) + " " + showTuple[t](t)
    }

  val aTupleString = showTuple(("Scala", 2, true)) // compiles and givens are injected at compile time
  // val thisWontCompile = showTuple(("Scala", List(1), 42)) // doesn't compile

  val aTuple: Tuple = ("Scala", true, 45)
  // val thisWontCompile = showTuple(aTuple) // will not compile, type is too general
  
  def main(args: Array[String]) = {
    println(aTupleString)
  }
}