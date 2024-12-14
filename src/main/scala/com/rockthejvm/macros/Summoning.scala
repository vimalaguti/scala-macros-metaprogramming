package com.rockthejvm.macros

import quoted.*

object Summoning {
  
  trait MyTypeClass[A] {
    def message: String
  }

  inline def describeType[A <: Tuple]: String = 
    ${ describeTypeImpl[A] }

  def describeTypeImplFaulty[A <: Tuple : Type](using Quotes): Expr[String] = 
    Type.of[A] match {
      case '[(_, a, _)] =>
        // summon the MyTypeClass[a]
        // we can't access a given MyTypeClass[a] because a is a type VARIABLE
        // '{ summon[MyTypeClass[a]].message } // this doesn't work
        Expr("can't summon instance")

      case _ => Expr("some type I don't know about")
    }

  // Expr.summon - delays the use of summon to the macro expansion site
  def describeTypeImpl[A <: Tuple : Type](using q: Quotes): Expr[String] = {
    import q.reflect.*

    Type.of[A] match {
      case '[(_, a, _)] =>
        val maybeTypeClass = Expr.summon[MyTypeClass[a]] // delay of summoning - returns an Option
        // can perform logic on whether this given exists
        val typeClass = maybeTypeClass.getOrElse(report.errorAndAbort(s"missing type class for ${Type.show[a]}"))

        '{ $typeClass.message }
    }
  }

}
