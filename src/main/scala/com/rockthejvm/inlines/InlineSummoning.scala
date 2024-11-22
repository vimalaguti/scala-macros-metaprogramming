package com.rockthejvm.inlines

import scala.compiletime.{summonInline, summonFrom}

object InlineSummoning {
  
  trait Semigroup[A] {
    def combine(a: A, b: A): A
  }

  def doubleSimple[A](a: A)(using Semigroup[A]): A = 
    summon[Semigroup[A]].combine(a, a)

  // val four = double(2) // doesn't compile

  // doesn't work with type class derivation + inlines

  // works 
  /* 
    Even though we don't have all the info about A,
    summonInline defers the act of summoning to the call site of the function,
    and the A type will be concrete to the compiler and it will be clear
    whether summoning is possible or not.
   */
  inline def double[A](a: A): A = 
    summonInline[Semigroup[A]].combine(a, a)

  given Semigroup[Int] = _ + _
  val four = double(2) // ok
  // val scalax2 = double("Scala") // not ok

  // conditional summoning - summonFrom
  trait Messenger[A] {
    def message: String
  }

  given Messenger[Int] with {
    override def message: String = "this is an int speaking"
  }

  /* 
    With summonFrom, we can conditionally produce values at compile time (inlined) depending on the givens the compiler finds.
    The pattern match will return the expression for the first matched given found at the call site.
  */
  inline def produceMessage[A] =
    summonFrom {
      case m: Messenger[A] => "Found messenger: " + m.message
      case _ => "Bummer, no messenger found for this type"
    }

  val intMessage = produceMessage[Int] // "Found messenger: " + Messenger[Int].message
  val otherMessage = produceMessage[String] // "Bummer, no messenger found for this type"
}
