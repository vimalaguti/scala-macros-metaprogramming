package com.rockthejvm.typesafejdbc

import quoted.*

trait ColumnMapping[T <: JDBCType.TL, N <: JDBCNullability.TL, C <: String] {
  type Result // left abstract, will be inferred by the compiler

  def reader: JDBCReader[Result]
}

object ColumnMapping {
  import JDBCNullability.TL.*

  type Aux[T <: JDBCType.TL, N <: JDBCNullability.TL, C <: String, R] =
    ColumnMapping[T,N,C] { type Result = R }

  // non-nullable column mappings
  given [C <: String]: ColumnMapping[JDBCType.TL.Integer, NonNullable, C] with {
    type Result = Int
    override def reader: JDBCReader[Int] = JDBCReader.int
  }
  given [C <: String]: ColumnMapping[JDBCType.TL.Double, NonNullable, C] with {
    type Result = Double
    override def reader: JDBCReader[Double] = JDBCReader.double
  }
  given [C <: String]: ColumnMapping[JDBCType.TL.Boolean, NonNullable, C] with {
    type Result = Boolean
    override def reader: JDBCReader[Boolean] = JDBCReader.boolean
  }
  given [C <: String]: ColumnMapping[JDBCType.TL.Varchar, NonNullable, C] with {
    type Result = String
    override def reader: JDBCReader[String] = JDBCReader.string
  }

  // nullable column mappings
  given [C <: String, T <: JDBCType.TL, R](using existingMapper: Aux[T, NonNullable, C, R]): ColumnMapping[T, Nullable, C] with {
    type Result = Option[R]
    override def reader: JDBCReader[Result] = existingMapper.reader.toOption
  }

  // array column mapping
  given [C <: String, T <: JDBCType.TL, R](using existingMapper: Aux[T, NonNullable, C, R]): ColumnMapping[JDBCType.TL.Array[T], NonNullable, C] with {
    type Result = List[R]
    override def reader: JDBCReader[Result] = existingMapper.reader.toList
  }

  def produceColumnMappingError[T: Type, N: Type, C: Type](using q: Quotes) = {
    import q.reflect.*
    
    given Printer[TypeRepr] = Printer.TypeReprShortCode
    val tType = TypeRepr.of[T].show
    val nType = TypeRepr.of[N].show
    val cType = TypeRepr.of[C].show

    report.errorAndAbort(s"Failed to summon a given ColumnMapping[$tType, $nType, $cType]")
  }
}

// val columnMapping = ColumnMapping[JDBCType.TL.Integer, JDBCNullability.TL.NonNullable, "id"]
// (type Result = Int, inferred by the compiler!)