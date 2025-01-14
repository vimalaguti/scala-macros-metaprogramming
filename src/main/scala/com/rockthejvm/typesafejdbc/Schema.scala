package com.rockthejvm.typesafejdbc

case class ColumnDescriptor(
  index: Int,
  name: String,
  jdbcType: JDBCType.VL,
  nullability: JDBCNullability.VL
)

final case class Schema(values: List[ColumnDescriptor])
