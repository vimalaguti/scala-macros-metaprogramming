package com.rockthejvm.typesafejdbc

trait JDBCReader[A] { self =>
  def toOption: JDBCReader[Option[A]] = (value: Any) => Option(self.read(value))
  def toList: JDBCReader[List[A]] = (value: Any) => 
    value.asInstanceOf[Array[Any]]
      .toList
      .map(v => self.read(v))
      
  def read(value: Any): A
}

object JDBCReader {
  def int: JDBCReader[Int] = (value: Any) => value.asInstanceOf[Int]
  def string: JDBCReader[String] = (value: Any) => value.asInstanceOf[String]
  def double: JDBCReader[Double] = (value: Any) => value.asInstanceOf[Double]
  def boolean: JDBCReader[Boolean] = (value: Any) => value.asInstanceOf[Boolean]
}