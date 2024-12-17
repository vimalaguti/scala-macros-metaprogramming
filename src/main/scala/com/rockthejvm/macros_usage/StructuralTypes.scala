package com.rockthejvm.macros_usage

import com.rockthejvm.macros.StructuralTypes.*

/* 
  Structural type = "compile time duck typing"
 */

class Person(val name: String, val age: Int)

object StructuralTypes {
  def makePerson(name: String): Person = new Person(name, 0)
  def makeProgrammer(name: String): Person { val favLanguage: String } = ???
  //                                ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ structural type

  val simpleRecord = Record.make(
    "name" -> "Daniel",
    "age" -> 99,
    "favLanguage" -> "Scala"
  )

  val name = simpleRecord.fields.getOrElse("name", "")
  // we would like to use the fields STATICALLY
  // (with auto-completion!)
  val nameStatic = simpleRecord.name // properly available and with the correct type!
  val ageStatic = simpleRecord.age
}
