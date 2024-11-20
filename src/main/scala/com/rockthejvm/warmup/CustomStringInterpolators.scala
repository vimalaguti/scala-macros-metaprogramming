package com.rockthejvm.warmup

object CustomStringInterpolators {

  // s-interpolator
  val pi = 3.14159
  val sInterpolator = s"The value of PI is appox ${pi + 0.000002}, the regular pi is $pi"

  // f-interpolator, similar to printf
  val fInterpolator = f"The value of PI up to 3 sig digits is $pi%3.2f"

  // raw-interpolator = escape sequences
  val rawInterpolator = raw"The value of pi is $pi\n this is NOT a newline"

  // sql"select * from ..."
  // $"first_name"

  case class Person(name: String, age: Int)

  // name,age => Person
  def string2Person(line: String): Person = {
    val tokens = line.split(",")
    Person(tokens(0), tokens(1).toInt)
  }

  // pers"$name,$age" -> Person("Daniel", 99)
  // StringContext + extension method
  extension(sc: StringContext)
    def pers(args: Any*): Person = {
      val concat = sc.s(args*) // using the known s-interpolator
      string2Person(concat)
    }

  val daniel = pers"Daniel,99" // Person instance
  val name = "Daniel"
  val age = 99
  val daniel_v2 = pers"$name,$age"

  def main(args: Array[String]): Unit = {
    println(sInterpolator)
    println(fInterpolator)
    println(rawInterpolator)
    println(daniel)
    println(daniel_v2)
  }
}
