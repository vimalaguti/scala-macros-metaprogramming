package com.rockthejvm.macros_usage

import com.rockthejvm.macros.QuoteMatching.*

object QuoteMatching {
  val a = pmOptions(Some(42))
  val b = pmOptions(None) 
  val c = pmOptions(Option(42)) // Option(42) != Some(42) as an EXPRESSION
  val d = pmOptions(new Some(42)) // new Some(42) != Some(42) as an EXPRESSION
  val e = pmOptions(Some(2 + 5)) // the expression Some(2 + 5) != Some(7)
  val f = pmGeneric(Some(45))
  val g = pmAny(Some("Scala"))
  val h = pmAny(Some(List(1,2,3)))
  val i = pmErasureAvoidance(List(1,2,4))
  val j = pmErasureAvoidance(List("Scala", "macros"))
  val k = pmErasureAvoidance(List('a', 'b', 'c'))
  val l = pmListExpression(List(1,2,3).map(_.toString).map(_.length))
  val m = pmListExpression(List("Scala", "is", "insane").map(_.length).map(_.toString))
}
