package com.rockthejvm.macros_usage

import com.rockthejvm.macros.MacroIntro.*

object MacroIntro {
  val firstMacroValue = firstMacro(42, "Scala")
  val secondMacroValue = firstMacro(2 + 3, "Scala") // arguments are computed before the macro expansion
  // the following will not compile
  val aNumber = 42
  val aString = "Scala"
  // val improperMacroValue = firstMacro(aNumber, aString) // error - the values are not compile-time computable

  // will expand the expressions literally in the macro implementation
  val inlineExpandedMacro = firstMacroIA(1 + aNumber / 4, "Scala".repeat(3))
}
