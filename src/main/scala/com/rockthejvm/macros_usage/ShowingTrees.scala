package com.rockthejvm.macros_usage

import com.rockthejvm.macros.ShowingTrees.*

object ShowingTrees {

  debugExpr(List(1,2,3).map(_.toString)) 
  debugExpr[Either[List[String], Int]](Right(3))
  val x = 42
  debugExpr(s"Scala is cool, meaning of life is $x")
}
