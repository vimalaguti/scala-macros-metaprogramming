package com.rockthejvm.wartimizer

import Wartimizer.wartimize

case class Person(name: String, email: String)

object WartimizerUsage {
  
  // normal Scala code
  val badPractice = "This is Scala: " + Person("Daniel", "daniel@rockthejvm.com")

  // this does not compile (good!)
  // val linted = wartimize(StringPlusAny)("This is Scala: " + Person("Daniel", "daniel@rockthejvm.com"))

  // this doesn't compile because the variable is not a compile-time known singleton instance (object)
  // val wartimizer: Wartimization = StringPlusAny 
  // val linted = wartimize(wartimizer)("This is Scala: " + Person("Daniel", "daniel@rockthejvm.com"))

  // validated code - should compile just fine
  val stringConcatenation = wartimize(StringPlusAny)("Scala is " + "AMAZING")

  def main(a: Array[String]) = {
    println(badPractice)
  }
}
