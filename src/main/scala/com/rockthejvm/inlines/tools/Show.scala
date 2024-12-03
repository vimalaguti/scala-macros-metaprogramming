package com.rockthejvm.inlines.tools

import compiletime.* 
import scala.deriving.Mirror

trait Show[A] {
  def show(a: A): String
}

object Show {
  given Show[String] with
    def show(a: String): String = a
  
  given Show[Int] with
    def show(a: Int): String = a.toString
  
  given Show[Boolean] with
    def show(a: Boolean): String = a.toString

  // auto-derivation for a serialization type class

  // showTuple[(String, Int, Boolean), ("name", "age", "programmer")](("Daniel", 99, true)) =>
  // ["name: Daniel", "age: 99", "programmer: true"]
  private inline def showTuple[E <: Tuple, L <: Tuple](elements: E): List[String] = 
    inline (elements, erasedValue[L]) match { // (("Daniel", 99, true), ("name", "age", "programmer"))
      case (EmptyTuple, EmptyTuple) => List()
      case (el: (eh *: et), lab: (lh *: lt)) => 
        val (h *: t) = el // h = "Daniel", t = (99, true)
        val label = constValue[lh] // label = "name"
        val value = summonInline[Show[eh]].show(h) // Show[String].show("Daniel")

        ("" + label + ": " + value) :: showTuple[et, lt](t)
        // "name: Daniel" :: showTuple[(Int, Boolean), ("age", "programmer")]((99, true))
    }

  /* 
    Necessary for type class derivation
    Signature
      - requires no arg list of its own
      - must return a Show[that particular type]
    */
  inline def derived[A <: Product](using m: Mirror.ProductOf[A]): Show[A] = 
    new Show[A] {
      // show(Person("Daniel", 99, true))
      override def show(value: A): String = 
        val valueTuple = Tuple.fromProductTyped(value) // ("Daniel", 99, true)
        val fieldReprs = showTuple[m.MirroredElemTypes, m.MirroredElemLabels](valueTuple) // ["name: Daniel", "age: 99", "programmer: true"]
        fieldReprs.mkString("{", ",", "}")
    }
}
