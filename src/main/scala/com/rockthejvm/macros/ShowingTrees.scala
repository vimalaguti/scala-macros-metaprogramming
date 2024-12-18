package com.rockthejvm.macros

import quoted.*

object ShowingTrees {
  inline def debugExpr[A](inline value: A) =
    ${ debugExprImpl('value) }

  def debugExprImpl[A: Type](value: Expr[A])(using q: Quotes): Expr[Unit] = {
    import q.reflect.*

    // typed: Expr[A], Type[A]
    // untyped: Term, TypeRepr
    val term = value.asTerm
    val typeRepr = TypeRepr.of[A]

    // prints will happen at compile time, before the expr is injected into the code
    println("============== start debug ===========================================")
    // with fully qualified class names
    println(term.show)
    // short types
    println(term.show(using Printer.TreeShortCode))
    // print the tree
    println(term.show(using Printer.TreeStructure))

    // print type reprs
    // showing fully qualified class names
    println(typeRepr.show) 
    // skip fqcn
    println(typeRepr.show(using Printer.TypeReprShortCode))
    // the AST structure
    println(typeRepr.show(using Printer.TypeReprStructure))

    println("============== end debug ===========================================")

    '{ () }
  }
}
