package com.rockthejvm.inlines

import deriving.Mirror
import compiletime.*
import com.rockthejvm.inlines.tools.Show

// "product"
case class Person(name: String, age: Int, programmer: Boolean)

// "sum"
enum Permissions {
  case READ, WRITE, EXECUTE
}

// automatically derive Show[A] where A can be any Sum type or Product type

object Mirrors {
  // mirror for a product type
  val personMirror = summon[Mirror.Of[Person]] // Mirror.ProductOf[Person]
  // mirror contains all type information

  val daniel: Person = personMirror.fromTuple(("Daniel", 99, true))
  val aTuple: (String, Int, Boolean) = Tuple.fromProductTyped(daniel)

  val className = constValue[personMirror.MirroredLabel] // the name of the class, known at compile time
  val fieldNames = constValueTuple[personMirror.MirroredElemLabels] // names of the fields

  // mirror of sum type
  val permissionMirror = summon[Mirror.Of[Permissions]] // Mirror.SumOf[Permissions]
  // we can get the type name
  // we can list all the cases
  val allCases = constValueTuple[permissionMirror.MirroredElemLabels] // all the cases of the enum as strings, known at compile time

  // auto-derivation for a serialization type class

  // showTuple[(String, Int, Boolean), ("name", "age", "programmer")](("Daniel", 99, true)) =>
  // ["name: Daniel", "age: 99", "programmer: true"]
  inline def showTuple[E <: Tuple, L <: Tuple](elements: E): List[String] = 
    inline (elements, erasedValue[L]) match { // (("Daniel", 99, true), ("name", "age", "programmer"))
      case (EmptyTuple, EmptyTuple) => List()
      case (el: (eh *: et), lab: (lh *: lt)) => 
        val (h *: t) = el // h = "Daniel", t = (99, true)
        val label = constValue[lh] // label = "name"
        val value = summonInline[Show[eh]].show(h) // Show[String].show("Daniel")

        ("" + label + ": " + value) :: showTuple[et, lt](t)
        // "name: Daniel" :: showTuple[(Int, Boolean), ("age", "programmer")]((99, true))
    }

  inline def showCC[A <: Product](using m: Mirror.ProductOf[A]): Show[A] = 
    new Show[A] {
      // show(Person("Daniel", 99, true))
      override def show(value: A): String = 
        val valueTuple = Tuple.fromProductTyped(value) // ("Daniel", 99, true)
        val fieldReprs = showTuple[m.MirroredElemTypes, m.MirroredElemLabels](valueTuple) // ["name: Daniel", "age: 99", "programmer: true"]
        fieldReprs.mkString("{", ",", "}")
    }

  val masterYoda = Person("Master Yoda", 800, false)
  val showPerson = showCC[Person]
  val yodaJson = showPerson.show(masterYoda)

  def main(a: Array[String]) = {
    println(yodaJson)
  }
}
