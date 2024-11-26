package com.rockthejvm.inlines

object CompileTimeOps {

  object Ints {
    import scala.compiletime.ops.int.*

    val two: 1 + 1 = 2
    val four: 2 * 2 = 4
    val truth: <=[3,4] = true
    val aString: ToString[2 * 4] = "8"
  }  

  object Booleans {
    import compiletime.ops.boolean.* // can omit "scala." prefix

    val lie: ![true] = false
    val combination: true && false = false
  }

  object Strings {
    import scala.compiletime.ops.string.*

    val aLiteral: "Scala" = "Scala"
    val aLength: Length["Scala"] = 5
    val regexMatching: Matches["Scala", ".*al*"] = true
  }

  // compile time values
  // constValue
  object Values {
    import compiletime.ops.int.*
    import compiletime.ops.string.{+ => _, *}
    import compiletime.{ constValue, constValueOpt }

    val five = constValue[2 + 3] // the type 5 => the VALUE 5
    val five_v2 = constValue[Length["Scala"]] // the type 5 => the value 5

    // anything other than a literal will fail
    // val anInt = constValue[Int] // does not work

    // constValueOpt will give you an Option, if you have a literal => Some, otherwise a None
    val fiveOpt = constValueOpt[2 + 3]
    val fiveNone = constValueOpt[Int]

    inline def customErrorCode[N <: Int] =
      compiletime.error("Error code: " + constValue[ToString[N]])

    // val customError = customErrorCode[6] // fails, for the right reason and with the right message
  }
}
