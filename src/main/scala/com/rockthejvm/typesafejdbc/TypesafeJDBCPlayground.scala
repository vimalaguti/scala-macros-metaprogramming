package com.rockthejvm.typesafejdbc

import scala.reflect.Selectable.reflectiveSelectable

object QueryMagic {
  def run[A] (query: String): List[A] = ???
}

object TypesafeJDBCPlayground {
  
  val query = "select * from users"

  // 1 - find the schema
  val schema = JDBCCommunication.getSchema(query)

  // 2 - identify column mappings for all columns (synthesized as givens)
  val idMapping: ColumnMapping[JDBCType.TL.Integer, JDBCNullability.TL.Nullable, "id"] = ??? // = summon[ColumnMapping[JDBCType.TL.Integer, JDBCNullability.TL.Nullable, "id"]] // fails because no given

  // 3 - infer the result type
  type RefinedResult = QueryResult {
    val id: idMapping.Result // same as Int
    // same for the rest of the columns (automatically)
  }

  // 4 - ability to read values from JDBC into the correct type
  val idColumnReader = idMapping.reader
  // same for the rest of the columns

  // 5 - run the query and return the correct type
  val magicResult = QueryMagic.run[RefinedResult](query)
  //                               ^^^^^^^^^^^^^ passed by the macro automatically

  // 6 - profit
  val ids = magicResult.map(_.id)
  
  def main(a: Array[String]) = 
    println("lock and load")
}
