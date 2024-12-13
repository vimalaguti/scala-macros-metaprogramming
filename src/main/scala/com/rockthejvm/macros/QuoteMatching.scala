package com.rockthejvm.macros

import quoted.*

object QuoteMatching {
  
  inline def pmOptions(inline opt: Option[Int]) = 
    ${ pmOptionsImpl('opt) }

  def pmOptionsImpl(opt: Expr[Option[Int]])(using Quotes): Expr[String] = {
    val result = opt match {
      case '{ Some(42) } => "got the meaning of life"
      case '{ Some($x) } => 
        // x is of type Expr[Any]
        s"got a variable: ${x.show}"
      case _ => "Got something else"
    }

    Expr(result)
  }

  // we can PM on generic types
  inline def pmGeneric[A](inline opt: Option[A]) = 
    ${ pmGenericImpl('opt) }

  def pmGenericImpl[A: Type](opt: Expr[Option[A]])(using Quotes): Expr[String] = {
    val result = opt match {
      case '{ Some($x) } => s"got a variable: ${x.show} of type ${Type.show[A]}"
      case _ => "Got something else"
    }

    Expr(result)
  }

  inline def pmAny(inline opt: Option[Any]) = 
    ${ pmAnyImpl('opt) }

  def pmAnyImpl(opt: Expr[Option[Any]])(using Quotes): Expr[String] = {
    val result = opt match {
      case '{ Some($x: String) } => s"got a string: ${x.show}"
      case '{ Some($x: Int) } => s"got an int: ${x.show}"
      case '{ Some($x) } => s"got a value: ${x.show}"
      case _ => "got something else"
    }

    Expr(result)
  }

  inline def pmErasureAvoidance(inline list: List[Any]) = 
    ${ pmErasureAvoidanceImpl('list) }

  def pmErasureAvoidanceImpl(list: Expr[List[Any]])(using Quotes): Expr[String] = {
    val result: String = list match {
      case '{ $l: List[Int] } => "a list of ints"
      // we magically get a given Type[t] when matched in a pattern
      case '{ $l: List[t] } => s"a list of elements of type ${Type.show[t]}"
      case _ => "a list of something else"
    }

    Expr(result)
  }

  // List(1,2,3).map[String](_.toString).map[Int](_.length)

  inline def pmListExpression(inline list: List[Any]) =
    ${ pmListExpressionImpl('list) }

  def pmListExpressionImpl(list: Expr[List[Any]])(using Quotes): Expr[String] = {
    val result: String = list match {
      case '{
        type t1 <: AnyVal // TYPE VARIABLE not an abstract type member in interface
        //      ^^^^^^^^^ can add type restrictions - pattern will match if this condition holds true
        ($l: List[`t1`]).map[t2]($f).map[`t1`]($g)
        //        ^^^^                   ^^^^ match EXACTLY that type value
        //        ^^^^ compiler ASSIGNS the type t1 with whatever the match returned
      } => s"got a chain of list maps between ${Type.show[t1]} and ${Type.show[t2]}"

      case _ => "got something else"
    }

    Expr(result)
  }
}
