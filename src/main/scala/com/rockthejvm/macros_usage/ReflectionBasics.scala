package com.rockthejvm.macros_usage

import com.rockthejvm.macros.ReflectionBasics.*
//

object ReflectionBasics {
  
  case class SimpleWrapper(x: Int) {
    def magicMethod(y: Int) =
      s"This simple wrapper calls a magic method with result ${x + y}"
  }

  val meaningOfLife = 42
  val descriptor = callMethodDynamically(SimpleWrapper(10), meaningOfLife, "magicMethod")
  // ^^ is transformed (at compile time) to SimpleWrapper(10).magicMethod(meaningOfLife)

  // compile time error - no method found
  // val descriptorFail = callMethodDynamically(SimpleWrapper(10), meaningOfLife, "someMethod")

  // compile time error - type mismatch
  val truth = true
  // val descriptorFail = callMethodDynamically(SimpleWrapper(10), truth, "magicMethod")

  // usage of the general tuple creator
  val myLittleTuple = createTuple[3, String]("Scala")

  def main(a: Array[String]) = {
    println(myLittleTuple)
  }
}
