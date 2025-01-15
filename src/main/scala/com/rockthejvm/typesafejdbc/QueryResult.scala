package com.rockthejvm.typesafejdbc

class QueryResult(colReaders: List[(String, JDBCReader[?])])(row: Row) extends Selectable {
  private val namedReaders: Map[String, JDBCReader[?]] =
    colReaders.toMap

  private def columnError(name: String) =
    throw new RuntimeException(s"invalid column selected: $name") // ok to throw an error if the column doesn't exist

  def selectDynamic(name: String): Any = {
    val reader = namedReaders.getOrElse(name, columnError(name))
    val value = row.values.getOrElse(name, columnError(name))
    reader.read(value)
  }
    // will return the correct value for the column name `name` out of a JDBC Row
}
