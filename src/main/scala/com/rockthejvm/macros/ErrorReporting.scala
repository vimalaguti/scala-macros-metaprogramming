package com.rockthejvm.macros

import quoted.*

object ErrorReporting {
  inline def funcWithErrors(x: Int) =
    ${ funcWithErrorsImpl('x) }

  def funcWithErrorsImpl(x: Expr[Int])(using q: Quotes): Expr[Int] = {
    // in order to run error reports, we need to import the quotes' reflect package
    import q.reflect.*

    if (x.valueOrAbort < 0) {
      // reports a compile time error + stops the compiler
      report.errorAndAbort(s"${x.show} is negative")
    }

    '{ $x + 3 }
  }

  inline def funcWithErrorsNoAbort(x: Int) = 
    ${ funcWithErrorsNoAbortImpl('x) }

  def funcWithErrorsNoAbortImpl(x: Expr[Int])(using q: Quotes): Expr[Int] = {
    import q.reflect.*

    val value = x.valueOrAbort

    // these errors will not be accumulated - the compiler will stop at the first one
    if (value < 0)
      report.error(s"${x.show} is negative")

    if (value < 10)
      report.error(s"${x.show} is not big enough")

    '{ $x + 3 }
  }

  inline def errorReport2(x: Int, y: Int) =
    ${ errorReport2Impl('x, 'y) }

  def errorReport2Impl(x: Expr[Int], y: Expr[Int])(using q: Quotes): Expr[Int] = {
    import q.reflect.*

    val xValue = x.valueOrAbort
    val yValue = y.valueOrAbort

    if (xValue < 0)
      report.error(s"First expression ${x.show} is negative", x)
    // report the error for this exact expression ------------^

    if (yValue < 10)
      report.error(s"Second expression ${y.show} is too small", y)

    '{ $x + $y }
  }
}
