package com.rockthejvm.typesafejdbc

trait ColumnMapping[T <: JDBCType.TL, N <: JDBCNullability.TL, C <: String] {
  type Result // left abstract, will be inferred by the compiler

  def reader: JDBCReader[Result]
}

// val columnMapping = ColumnMapping[JDBCType.TL.Integer, JDBCNullability.TL.NonNullable, "id"]
// (type Result = Int, inferred by the compiler!)