package com.rockthejvm.inlines.tools

trait Show[A] {
  def show(a: A): String
}

object Show {
  given Show[String] with
    def show(a: String): String = a
  
  given Show[Int] with
    def show(a: Int): String = a.toString
  
  given Show[Boolean] with
    def show(a: Boolean): String = a.toString
}
