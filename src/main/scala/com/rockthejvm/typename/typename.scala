package com.rockthejvm.typename

import scala.quoted.*

case class TypeName[A](value: String)

object TypeName {
  def apply[A: TypeName]: TypeName[A] = summon[TypeName[A]]

  // compiler will synthesize new givens on the fly, upon request
  // 1 - macro expansion
  inline given make[A]: TypeName[A] = 
    ${ makeImpl[A] }

  // 2 - macro impl
  def makeImpl[A: Type](using Quotes): Expr[TypeName[A]] = {
    // given t: Type[A] = summon...
    val typeDescription: Expr[String] = Expr(Type.show[A])
    /* 
      Building exprs
      - quotes: the restrictions on variables & level consistency
      - apply method: only available for standard types
     */

    '{ TypeName[A]($typeDescription) }
  }
}
/* 
  val typename = TypeName[List[Option[Try[String]]]]
  println(typename.value) // List[Option[Try[String]]]
 */
