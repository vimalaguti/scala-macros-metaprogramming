package com.rockthejvm.macros

import quoted.*
import scala.util.Try

object TypeQuoteMatching {
  inline def matchType[A]: String =
    ${ matchTypeImpl[A] }

  def matchTypeImpl[A: Type](using Quotes): Expr[String] = {
    val typeOfA = Type.of[A]
    val result: String = typeOfA match {
      case '[Int] => "the int type"
      case '[List[Int]] => "a list of integers"
      case '[List[a]] => s"a list of ${Type.show[a]}" // `a` is a type VARIABLE
      case '[Either[a, b]] => s"Either with ${Type.show[a]} or ${Type.show[b]}"
      case '[a => b] => s"a function type from ${Type.show[a]} to ${Type.show[b]}"
      // can have type restrictions
      // available since Scala 3.5
      case '[type a; (`a`, b, `a`)] => s"a tuple with 3 type members, first/third are the same: ${Type.show[a]}"
      // can have type variables with bounds
      case '[type a <: AnyVal; Try[`a`]] => s"a try of plain value: ${Type.show[a]}"
      case _ => "something I don't know about"
    }

    Expr(result)
  }
}
