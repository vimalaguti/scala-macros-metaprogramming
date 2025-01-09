package com.rockthejvm.wartimizer

import Wartimizer.wartimize

case class Person(name: String, email: String)

object WartimizerUsage {
  
  // normal Scala code
  val badPractice = "This is Scala: " + Person("Daniel", "daniel@rockthejvm.com")

  // this does not compile (good!)
  // val linted = wartimize(StringPlusAny)("This is Scala: " + Person("Daniel", "daniel@rockthejvm.com"))

  // this doesn't compile because the variable is not a compile-time known singleton instance (object)
  // val wartimizer: Wartimization = StringPlusAny 
  // val linted = wartimize(wartimizer)("This is Scala: " + Person("Daniel", "daniel@rockthejvm.com"))

  // validated code - should compile just fine
  val stringConcatenation = wartimize(StringPlusAny)("Scala is " + "AMAZING")

  val firstEven = List(1,2,3,4,5).filter(_ % 2 == 0).headOption
  // rewritten to list.find(_ % 2 == 0)
  // val firstEven_v2 = wartimize(CollectionOptimizer)(List(1,2,3,4,5).filter(_ % 2 == 0).headOption)

  // does not compile because of StringPlusAny - good!
  // val combinedWartimized = wartimize(StringPlusAny, CollectionOptimizer)(List(1,2,3,4,5).filter(_ % 2 == 0).headOption.map("Scala " + _))

  // Map.get.getOrElse
  val simpleMap = Map("Alice" -> 123, "Bob" -> 456, "Charlie" -> 789)
  val personNumber = simpleMap.get("Martin").getOrElse(999)
  val personNumber_v2 = wartimize(CollectionOptimizer)(simpleMap.get("Martin").getOrElse(999))

  // successive map transformations
  val mappedList = List(1,2,3).map(_ + 1).map(_ * 3).map(_.toString + " Scala is great")
  val optimizedList = wartimize(CollectionOptimizer)(List(1,2,3).map(_ + 1).map(_ * 3).map(_.toString + " Scala is great"))

  val combinedCalls = List(1,2,3).map(_ + 1).map(_ * 3).map(_.toString + " Scala is great").filter(_.length > 5).headOption
  val optimizedCombinedCalls = wartimize(CollectionOptimizer)(List(1,2,3).map(_ + 1).map(_ * 3).map(_.toString + " Scala is great").filter(_.length > 5).headOption)

  // use foreach instead of map
  // should not compile
  // wartimize(CollectionOptimizer)(List(1,2,3).map(println))

  def main(a: Array[String]) = {
    println(badPractice)
  }
}
