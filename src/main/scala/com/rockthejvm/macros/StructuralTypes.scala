package com.rockthejvm.macros

import quoted.*

object StructuralTypes {

  class Record(val fields: Map[String, Any]) extends Selectable {
    // any refinement of this type will have access to statically available fields
    // with a method selectDynamic

    def selectDynamic(fieldName: String): Any = 
      fields(fieldName)
  }

  // works for explicitly defined structural types based on Record
  def demoStructuralType(): Unit =  {
    type Car = Record { val make: String; val model: String }
    val car: Car = ???
    val carModel = car.model // actually calls Record.selectDynamic("model")
  }
  
  // but we would like to GENERATE the appropriate structural type based on the fields

  object Record {
    // padawan: return Record(fields.toMap)
    // jedi master: return Record(fields.toMap) { val name: String = "Daniel"; val age: Int = 99; val favLanguage: String = "Scala" }
    transparent inline def make(inline fields: (String, Any)*): Record = 
      ${ makeImpl('fields) }

    def makeImpl(fields: Expr[Seq[(String, Any)]])(using q: Quotes): Expr[Record] = {
      import q.reflect.*

      val parentType = TypeRepr.of[Record]
      // refinement with a field "jediLevel": String
      val fieldName = "jediLevel"
      val fieldType = TypeRepr.of[String]
      val refinement = Refinement(parentType, fieldName, fieldType) // type representation of Record { val jediLevel: String }
      // if you want to add multiple fields, just refine further from the latest one

      val resultType = fields match {
        case Varargs(list) =>
          list.foldLeft(parentType) { (refinement, field) =>
            field match {
              case '{ ($nameExpr: String, $value: a) } =>
                // build a new refinement
                val name = nameExpr.value.getOrElse(badFieldError(nameExpr))
                val tpe = TypeRepr.of[a]
                Refinement(refinement, name, tpe)

              case '{ ($nameExpr: String) -> ($value: a) } =>
                // same logic
                val name = nameExpr.value.getOrElse(badFieldError(nameExpr))
                val tpe = TypeRepr.of[a]
                Refinement(refinement, name, tpe)
            }
          }

        case _ => report.errorAndAbort(s"Cannot build refinement type for ${fields}")
      }

      // resultType: TypeRepr
      // I need the COMPLETE type Record { val ...; val ... }
      resultType.asType match {
        case '[type r <: Record; r] =>
          '{ Record($fields.toMap).asInstanceOf[r] }
      }
    }
  }

  def badFieldError(expr: Expr[?])(using q: Quotes) = {
    import q.reflect.* 
    report.errorAndAbort(s"Only literal values and compile-time computable expressions allowed. Got ${expr.show}")
  }

}
