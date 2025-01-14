package com.rockthejvm.typesafejdbc

object JDBCType {
  // value level
  enum VL {
    // supported JDBC types as enum values to insert into a Schema instance
    case Integer
  }

  // type level
  // supported JDBC types as Scala types to pass as type arguments to ColumnMapping
  sealed trait TL
  object TL {
    sealed trait Integer extends JDBCType.TL
  }
}
