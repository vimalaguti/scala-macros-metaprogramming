package com.rockthejvm.wartimizer

import quoted.*

object CollectionOptimizer extends Wartimization {
  override def treeMap(using q: Quotes): q.reflect.TreeMap = {
    import q.reflect.*

    new q.reflect.TreeMap {
      override def transformTerm(tree: Term)(owner: Symbol): Term = {
        val maybeExpr = Option.when(tree.isExpr)(tree).map(_.asExpr)
        val maybeTransformedExpr = maybeExpr.collect {
          case '{ ($x: collection.Map[k, v]).get($key).getOrElse($value) } =>
            '{ $x.getOrElse($key, $value) }
          case '{ ($x: collection.Iterable[t1]).map[t2]($f).map[t3]($g) } =>
            '{ $x.map(a => $g($f(a))) }
          case '{ ($x: collection.Iterable[t]).filter($f).headOption } =>
            '{ $x.find($f) }
          case '{ ($x: collection.Iterable[t]).filter($f).size } =>
            '{ $x.count($f) }
          case '{ ($x: collection.Iterable[t]).collect($f).headOption } =>
            '{ $x.collectFirst($f) }
          case '{ ($x: collection.Iterable[t]).map[Unit]($f) } =>
            report.errorAndAbort("Use `foreach` instead of `map`", tree.pos)
        }

        maybeTransformedExpr
          .map(_.asTerm) // Option of processed term
          .map(term => transformTerm(term)(owner)) // continue processing until there's nothing left
          .getOrElse(super.transformTerm(tree)(owner)) // base case
      }
    }
  }
}
