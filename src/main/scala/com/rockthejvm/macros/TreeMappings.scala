package com.rockthejvm.macros

import scala.quoted.*

object TreeMappings {
  
  inline def transformCode[A](inline code: A): A = 
    ${ transformCodeImpl('code) }

  def transformCodeImpl[A: Type](code: Expr[A])(using q: Quotes): Expr[A] = {
    import q.reflect.*

    val treeMap = new TreeMap {
      // various transformation methods, which we can override
      override def transformTerm(tree: Term)(owner: Symbol): Term = {
        println("---------------------- TERM")
        println(tree.show(using Printer.TreeStructure))
        println(tree.show(using Printer.TreeShortCode))
        println("---------------------- /TERM")

        super.transformTerm(tree)(owner) // leave the term untouched
      }

      override def transformStatement(tree: Statement)(owner: Symbol): Statement = {
        println("---------------------- STATEMENT")
        println(tree.show(using Printer.TreeStructure))
        println(tree.show(using Printer.TreeShortCode))
        println("---------------------- /STATEMENT")

        super.transformStatement(tree)(owner) // leave the statement untouched
      }
    }

    treeMap.transformTerm(code.asTerm)(Symbol.spliceOwner).asExprOf[A]
  }

  inline def flipBooleans[A](inline code: A): A =
    ${ flipBooleansImpl('code) }

  def flipBooleansImpl[A: Type](code: Expr[A])(using q: Quotes): Expr[A] = {
    import q.reflect.*

    val treeMap = new TreeMap {
      override def transformTerm(tree: Term)(owner: Symbol): Term = 
        tree match {
          case Literal(BooleanConstant(value)) => Literal(BooleanConstant(!value))
          case _ => super.transformTerm(tree)(owner) // recurse until we get to a "leaf" node
        }

      override def transformStatement(tree: Statement)(owner: Symbol): Statement = 
        tree match {
          // value definitions => flip the expression on the right-hand side
          case vd @ ValDef(_, typeTree, Some(rhs)) if typeTree.tpe =:= TypeRepr.of[Boolean] =>
            // careful: we need to maintain the owner of the value definition
            given Quotes = vd.symbol.asQuotes // maintains the owner of the new ValDef

            val newRhs = '{ !${ rhs.asExprOf[Boolean] } }.asTerm
            ValDef(vd.symbol, Some(newRhs))

          // method definitions => flip the impl
          case dd @ DefDef(_, params, typeTree, Some(rhs)) if typeTree.tpe =:= TypeRepr.of[Boolean] =>
            given Quotes = dd.symbol.asQuotes

            val newRhs = '{ !${ rhs.asExprOf[Boolean] } }.asTerm
            DefDef(dd.symbol, _ => Some(newRhs))
          
          case _ => super.transformStatement(tree)(owner)
        }
    }

    treeMap.transformTerm(code.asTerm)(Symbol.spliceOwner).asExprOf[A]
  }

  inline def demoAccumulator[A](inline code: A): List[String] =
    ${ demoAccumulatorImpl('code) }
    
  def demoAccumulatorImpl[A: Type](code: Expr[A])(using q: Quotes): Expr[List[String]] = {
    import q.reflect.*
      
    // essentially like a foldLeft on a AST
    // useful for static code analysis at compile
    val treeAcc = new TreeAccumulator[List[String]] {
      override def foldTree(accumulator: List[String], tree: Tree)(owner: Symbol): List[String] = 
        if (tree.isExpr)
          tree.asExpr match {
            case '{ println($x) } =>
              x.show :: accumulator
            case _ => foldOverTree(accumulator, tree)(owner) // base case: just recurse
          }
        else foldOverTree(accumulator, tree)(owner)
    }

    val result = treeAcc.foldTree(List(), code.asTerm)(Symbol.spliceOwner)
    Expr(result)
  }
}
