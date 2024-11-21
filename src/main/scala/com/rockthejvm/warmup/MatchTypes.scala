package com.rockthejvm.warmup

import jdk.swing.interop.LightweightFrameWrapper

object MatchTypes {

  def lastDigitOf(number: BigInt): Int = (number % 10).toInt

  def lastCharOf(string: String): Char =
    if (string.isEmpty) throw new NoSuchElementException
    else string.charAt(string.length - 1)

  def lastElemOf[A](list: List[A]): A =
    if (list.isEmpty) throw new NoSuchElementException
    else list.last

  type ConstituentPartOf[A] = A match {
    case BigInt => Int
    case String => Char
    case List[a] => a
  }

  val aNumber: ConstituentPartOf[BigInt] = 2
  val aChar: ConstituentPartOf[String] = 'a'
  val anElement: ConstituentPartOf[List[Int]] = 42

  def lastPartOf[A](thing: A): ConstituentPartOf[A] = thing match {
    case number: BigInt => (number % 10).toInt
    case string: String =>
      if (string.isEmpty) throw new NoSuchElementException
      else string.charAt(string.length - 1)
    case list: List[_] =>
      if (list.isEmpty) throw new NoSuchElementException
      else list.last
  }

  val lastPartOfString = lastPartOf("Scala") // 'a'

  // recursion on match types
  type LowestLevelPartOf[A] = A match {
    case List[a] => LowestLevelPartOf[a]
    case _ => A
  }

  val lastPartOfNestedList: LowestLevelPartOf[List[List[List[Int]]]] = 10

  // compiler can detect cycles in match types
  //  type AnnoyingMatchType[A] = A match {
  //    case _ => AnnoyingMatchType[A]
  //  }

  // can crash recursion
  //  type InfiniteRecursion[A] = A match {
  //    case Int => InfiniteRecursion[A]
  //  }
  //
  //  val crash: InfiniteRecursion[Int] = 24

  def main(args: Array[String]): Unit = {

  }
}
