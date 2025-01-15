package com.rockthejvm.typesafejdbc

import scala.reflect.Selectable.reflectiveSelectable

object TypesafeJDBCPlayground {

  def getValues[T <: JDBCType.TL, N <: JDBCNullability.TL, C <: String](rows: List[Row], colName: String)(using mapper: ColumnMapping[T, N, C]): List[mapper.Result] =
    rows
      .map(_.values)
      .map(_.apply(colName))
      .map(v => mapper.reader.read(v))

  def demoJDBCReaders() = {
    val rows = JDBCCommunication.runQuery("select * from users")
    rows.foreach(println)
    val names = getValues[JDBCType.TL.Varchar, JDBCNullability.TL.NonNullable, "name"](rows, "name")
    names.foreach(println)
    val ages = getValues[JDBCType.TL.Integer, JDBCNullability.TL.Nullable, "age"](rows, "age")
    ages.foreach(println)
    val hobbies = getValues[JDBCType.TL.Array[JDBCType.TL.Varchar], JDBCNullability.TL.NonNullable, "hobbies"](rows, "hobbies")
    hobbies.foreach(println)
  }

  def demoRefinedType() = {
    inline val query = "select * from users"
    val decoder = QueryResultDecoder.make(query)
    val rows = JDBCCommunication.runQuery(query)
    val typedRows = rows.map(decoder.decode)
    val names = typedRows.map(_.name)
    names.foreach(println)
  }

  def demoRefinedType_v2 = {
    inline val query = "select * from users"
    val typedRows = QueryResultDecoder.run(query)
    typedRows.map(_.name).foreach(println)
  }

  def main(a: Array[String]) = {
    demoRefinedType()
  }
}
