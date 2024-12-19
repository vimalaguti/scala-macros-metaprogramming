package com.rockthejvm.macros_usage

import com.rockthejvm.macros.ValueDefs.*

object ValueDefs {
  /* 
    synthesized:
    scalaLength = {
      lazy val myValue = "Scala".length // new value definition
      myValue * 4
    }
   */
  val scalaLength = buildValueDef
}
