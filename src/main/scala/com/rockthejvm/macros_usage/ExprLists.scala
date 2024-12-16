package com.rockthejvm.macros_usage

import com.rockthejvm.macros.ExprLists.*

object ExprLists {
  val varargDescriptor = processVarargs(1 * 2 * 3, 3 + 45, 99)

  val listOfExpressions = returnExprs()
}
