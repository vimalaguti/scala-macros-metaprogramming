package com.rockthejvm.macros

import quoted.*

object TreeMatching {
  inline def demoTreeMatching[A](inline value: A): Unit = 
    ${ demoTreeMatchingImpl[A]('value) }

  def demoTreeMatchingImpl[A: Type](value: Expr[A])(using q: Quotes): Expr[Unit] = {
    import q.reflect.*

    val term = value.asTerm

    println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=")
    println(term.show(using Printer.TreeStructure))
    
    term match {
      case Inlined(_, _, Apply(Ident(funcName), args)) =>
        println(s"Function call: ${funcName} with arguments: ")
        args.foreach(arg => println(arg.show))
      // you can pattern match this term against any AST structure in Scala
      case _ => 
    }

    println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=")
    '{ () }
  }
}
