package com.rockthejvm.wartimizer

import quoted.*

// wartimization that doesn't compile any expression of type string + something, where something is not a string
object StringPlusAny extends Wartimization {
  override def treeMap(using q: Quotes): q.reflect.TreeMap = {
    import q.reflect.*

    new q.reflect.TreeMap {
      override def transformTerm(tree: Term)(owner: Symbol): Term = {
        // this term must be an Expr
        // this expr must be a String + something else
        if (!tree.isExpr) super.transformTerm(tree)(owner)
        else tree.asExpr match {
          case '{ ($lhs: String) + ($rhs: t) } if !(TypeRepr.of[t] <:< TypeRepr.of[String]) =>
            report.errorAndAbort("Adding String to anything else is forbidden", tree.pos)
          case _ => super.transformTerm(tree)(owner)
        }
      }
    }
  }
}
