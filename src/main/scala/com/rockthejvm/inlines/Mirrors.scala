package com.rockthejvm.inlines

import deriving.Mirror
import compiletime.*
import com.rockthejvm.inlines.tools.Show

// "product"
case class Person(name: String, age: Int, programmer: Boolean) derives Show
// compiler will look for a method `derived` in the Show object
// such that it returns a Show[Person]
// `derives Show` will give us a GIVEN Show[Person]

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

  val masterYoda = Person("Master Yoda", 800, false)
  val showPerson = Show.derived[Person] // explicit call
  val showPerson_v2 = summon[Show[Person]] // implicit call
  val showPerson_v3 = Person.derived$Show // explicit type class instance, synthesized by the compiler
  
  val yodaJson = showPerson.show(masterYoda)

  def printThing[A](thing: A)(using Show[A]) =
    println(summon[Show[A]].show(thing))

  def main(a: Array[String]) = {
    printThing(masterYoda) // <-- Show[Person] passed implicitly here
  }
}
