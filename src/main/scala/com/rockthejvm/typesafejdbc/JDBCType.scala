package com.rockthejvm.typesafejdbc

object JDBCType {
  // value level
  enum VL {
    // supported JDBC types as enum values to insert into a Schema instance
    case Integer, Double, Boolean, Varchar
    case Array(typ: JDBCType.VL)
    case NotSupported
  }

  // type level
  // supported JDBC types as Scala types to pass as type arguments to ColumnMapping
  sealed trait TL
  object TL {
    sealed trait Integer extends JDBCType.TL
    sealed trait Double extends JDBCType.TL
    sealed trait Boolean extends JDBCType.TL
    sealed trait Varchar extends JDBCType.TL
    sealed trait Array[T <: JDBCType.TL] extends JDBCType.TL
    sealed trait NotSupported extends JDBCType.TL
  }
}
