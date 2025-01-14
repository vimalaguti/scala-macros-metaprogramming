package com.rockthejvm.typesafejdbc

import java.sql.ResultSetMetaData
import java.sql.Types
import java.sql.ResultSet

case class ColumnDescriptor(
  index: Int,
  name: String,
  jdbcType: JDBCType.VL,
  nullability: JDBCNullability.VL
)

final case class Schema(values: List[ColumnDescriptor])

object Schema {
  def fromMetadata(metadata: ResultSetMetaData): Schema = {
    val descriptors = for {
      i <- 1 to metadata.getColumnCount()
    } yield ColumnDescriptor(
      index = i,
      name = metadata.getColumnLabel(i),
      jdbcType = getType(metadata, i),
      nullability = getNullable(metadata, i)
    )

    Schema(descriptors.toList)
  }

  private def getType(metadata: ResultSetMetaData, i: Int): JDBCType.VL = 
    metadata.getColumnType(i) match {
      case Types.VARCHAR => JDBCType.VL.Varchar
      case Types.INTEGER => JDBCType.VL.Integer
      case Types.DOUBLE => JDBCType.VL.Double
      case Types.BOOLEAN => JDBCType.VL.Boolean
      case Types.ARRAY if metadata.getColumnTypeName(i).contains("varchar") => 
        JDBCType.VL.Array(JDBCType.VL.Varchar)
      // FIXME: add all other cases if you want this library production-ready
      case _ => 
        println(s"Not supported: type ${metadata.getColumnType(i)} and ${metadata.getColumnTypeName(i)}")
        JDBCType.VL.NotSupported
    }
    
  private def getNullable(metadata: ResultSetMetaData, i: Int): JDBCNullability.VL = 
    metadata.isNullable(i) match {
      case ResultSetMetaData.columnNoNulls => JDBCNullability.VL.NonNullable
      case _ => JDBCNullability.VL.Nullable // assuming nullable in all other cases
    }
}
