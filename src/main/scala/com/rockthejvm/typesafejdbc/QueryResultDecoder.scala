package com.rockthejvm.typesafejdbc

import quoted.*

trait QueryResultDecoder[A] {
  type Result = A
  def decode(row: Row): A
}

object QueryResultDecoder {

  transparent inline def run(inline query: String): List[?] = 
    ${ runImpl('query) }

  // copied from `makeImpl` + returning a nice list
  def runImpl(query: Expr[String])(using Quotes): Expr[List[?]] = {
    val schema = JDBCCommunication.getSchema(query.valueOrAbort)
    val descriptorToMappings = schema.values.map(descriptorToMapping)
    val refinedType = makeRefinedType(descriptorToMappings)
    val columnReaders = getColumnReaders(descriptorToMappings)
    val decoder = makeDecoder(columnReaders, refinedType)
    
    refinedType match {
      case '[t] =>
        '{
          JDBCCommunication.runQuery($query).map($decoder.decode).asInstanceOf[List[t]]
        }
    }
  }

  transparent inline def make(inline query: String): QueryResultDecoder[?] = 
    ${ makeImpl('query) }

  def makeImpl(query: Expr[String])(using q: Quotes): Expr[QueryResultDecoder[?]] = {
    // 1 - find the schema
    // don't gasp at me connecting to the database at compile time
    val schema = JDBCCommunication.getSchema(query.valueOrAbort)

    // 2 - get all column mappings for all column descriptors in the schema
    val descriptorToMappings = schema.values.map(descriptorToMapping)

    // 3 - build the correct type refinement
    val refinedType = makeRefinedType(descriptorToMappings)
    /* 
        type RefinedResult = QueryResult {
          val id: idMapping.Result
          val name: nameMapping.Result 
          // all other columns
        }
     */

    // 4 - get the column readers
    val columnReaders = getColumnReaders(descriptorToMappings)

    // 5 - get the query decoder
    makeDecoder(columnReaders, refinedType)
  }

  // fetches the correct given ColumnMapping for this column
  private def descriptorToMapping(descriptor: ColumnDescriptor)(using Quotes): DescriptorToMapping = {
    // JDBCType.VL.Varchar => Type[JDBCType.TL.Varchar]
    val jdbcType = toType(descriptor.jdbcType)
    val nullability = toType(descriptor.nullability)
    val colType = toType(descriptor.name)

    (jdbcType, nullability, colType) match {
      case (
        '[type t <: JDBCType.TL; `t`],
        '[type n <: JDBCNullability.TL; `n`],
        '[type c <: String; `c`]
      ) => 
        val mapping = Expr.summon[ColumnMapping[t,n,c]].getOrElse(ColumnMapping.produceColumnMappingError[t,n,c])
        DescriptorToMapping(descriptor, mapping)
    }
  }

  // create a QueryResult { the correct refinements } based on all column descriptors and mappings
  private def makeRefinedType(descriptorToMappings: List[DescriptorToMapping])(using q: Quotes): Type[?] = {
    import q.reflect.*

    val refined = descriptorToMappings.foldLeft(TypeRepr.of[QueryResult]) {
      case (currentRef, dtm) => 
        // add a new field to currentRef of the form `val $name: $type`
        val name = dtm.descriptor.name
        val typeRepr = dtm.mapping match {
          case '{ $mapping: ColumnMapping.Aux[_, _, _, colType] } =>
            TypeRepr.of[colType]
        }

        Refinement(currentRef, name, typeRepr)
    }

    refined.asType
  }

  // fetches all readers of the correct type so the values can be read correctly into the correct types
  private def getColumnReaders(descriptorToMappings: List[DescriptorToMapping])(using Quotes): Expr[List[(String, JDBCReader[?])]] = {
    val jdbcReaders = descriptorToMappings.map { dtm =>
      val nameExpr = Expr(dtm.descriptor.name)

      dtm.mapping match {
        case '{ $mapping: ColumnMapping.Aux[_, _, _, colType] } =>
          '{ $nameExpr -> $mapping.reader }
      }
    }

    Expr.ofList(jdbcReaders)
  }

  // get the final decoder that can read entire rows into the structural type inferred earlier
  private def makeDecoder(columnReaders: Expr[List[(String, JDBCReader[?])]], refinedType: Type[?])(using Quotes): Expr[QueryResultDecoder[?]] = 
    refinedType match {
      case '[r] =>
        '{  new QueryResultDecoder[r] {
              type Result = r
              override def decode(row: Row): r = 
                QueryResult($columnReaders)(row).asInstanceOf[r]
            } 
          } 
    }

  private def toType(vlType: JDBCType.VL)(using q: Quotes): Type[? <: JDBCType.TL] = {
    import q.reflect.*

    vlType match {
      case JDBCType.VL.Boolean => Type.of[JDBCType.TL.Boolean]
      case JDBCType.VL.Double => Type.of[JDBCType.TL.Double]
      case JDBCType.VL.Integer => Type.of[JDBCType.TL.Integer]
      case JDBCType.VL.Varchar => Type.of[JDBCType.TL.Varchar]
      case JDBCType.VL.Array(content) => 
        val inner = toType(content)
        inner match {
          case '[type i <: JDBCType.TL; `i`] =>
            Type.of[JDBCType.TL.Array[i]]
        }
      case JDBCType.VL.NotSupported => Type.of[JDBCType.TL.NotSupported]
    }
  }

  private def toType(vlType: JDBCNullability.VL)(using q: Quotes): Type[? <: JDBCNullability.TL] = {
    import q.reflect.*

    vlType match {
      case JDBCNullability.VL.NonNullable => Type.of[JDBCNullability.TL.NonNullable]
      case JDBCNullability.VL.Nullable => Type.of[JDBCNullability.TL.Nullable]
    }
  }

  private def toType(value: String)(using q: Quotes): Type[?] = {
    import q.reflect.*

    ConstantType(StringConstant(value)).asType
  }

  case class DescriptorToMapping(
    descriptor: ColumnDescriptor,
    mapping: Expr[ColumnMapping[?, ?, ?]]
  )
}