package com.rockthejvm.macros

import quoted.*

object Quoting {
  // part 1 of the macro pattern
  inline def runPlayground(inline string: String) =
    ${ macroPlayground('string) }

  // part 2 - macro implementation
  def macroPlayground(stringExpr: Expr[String])(using Quotes): Expr[String] = {
    // code <--> Expr
    // quoting == code -> Expr
    // splicing == Expr -> code

    // I can quote expressions even INSIDE a macro implementation
    val anExpr: Expr[String] = '{ "some constant string" }
    // I can quote and splice at multiple levels
    val moreComplexExpr: Expr[String] = '{ "more complex string: " + $stringExpr }

    // exprs are typed
    val lostTypeInfo: Expr[Any] = '{ "Scala" } // not an Expr[String] but something wider
    // val aQuotedExpr = '{ $lostTypeInfo.drop(1) + "!" } // does not compile

    // in regular code - asInstanceOf
    // val a: Any = "Scala"
    // val b = a.asInstanceOf[String]

    // in Expr - asExprOf
    val recoveredTypeInfo = lostTypeInfo.asExprOf[String]
    val aQuotedExprRecovered = '{ $recoveredTypeInfo.drop(1) + "!" }

    val aSimpleString = "Rock the JVM".repeat(10000) // "level 0"
    // level consistency
    // val anExprWithSimpleString = '{ // quoting starts a new "level"
    //   "The Scala learning platform is " + aSimpleString // illegal
    //   // a variable defined at a level cannot be (provably) expanded at another level
    // }

    // correct way of creating expressions for further quotes
    // 1 - quote that expression
    // 2 - when you start a new level, expand/splice that quoted expression
    val aSimpleExpr = '{ "Rock the JVM".repeat(10000) } // Expr
    val anExprWithString = '{ // one level down, I can expand/splice that Expr
      "The Scala learnig platform is " + $aSimpleExpr
    }

    // open a quote -> increase the level
    // splice an expr -> decrease the level
    // val nestedExpr = '{ // level 1
    //   val a = "Scala"
    //   // in order to run a nested quote (level 1), we need to provide a given Quotes in this scope
    //   given q1: Quotes = ??? // impossible to create manually, will be passed by the compiler to methods of the form myMacro(myArg: myType)(using Quotes)
    //   '{ // level 2
    //     val b = "is"
    //     '{ // level 3
    //       val c = "great"
    //       '{ // level 4
    //         a + b + c + ${ // level 3
    //           aSimpleExpr
    //         }
    //       }
    //     }
    //   }
    // }

    anExprWithString
  }
}
