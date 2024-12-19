package com.rockthejvm.macros

import quoted.*

object ValueDefs {
  inline def buildValueDef = 
    ${ buildValueDefImpl }

  // val x: Int = "Scala".length
  def buildValueDefImpl(using q: Quotes): Expr[Int] = {
    import q.reflect.*

    // identifier = symbol
    val mySymbol = Symbol.newVal(
      parent = Symbol.spliceOwner, // synthesize new symbols within this parent (in this case, the macro is the owner)
      name = "myValue", // the name of the new val
      tpe = TypeRepr.of[Int], // type representation
      flags = Flags.Lazy, // any flags you want (e.g. lazy, inline, private, ...)
      privateWithin = Symbol.noSymbol // None for symbols
    )

    val valBody = {
      // technical detail: the given Quotes should be given by the symbol
      given Quotes = mySymbol.asQuotes // equivalent to saying that this term is "owned" by the mySymbol

      '{ "Scala".length }.asTerm
    }

    // val myValue: Int = "Scala".length
    val valueDef = ValDef(symbol = mySymbol, rhs = Some(valBody))

    // myValue * 4 => refer to the value def
    val valRef = Ref(mySymbol).asExprOf[Int]

    // expression myValue * 4
    val finalExpr = '{ $valRef * 4 }

    Block(
      stats = List(valueDef), // all the definitions of the block (e.g. values, methods, etc)
      expr = finalExpr.asTerm
    ).asExprOf[Int]
  }
}
