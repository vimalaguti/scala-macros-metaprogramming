package com.rockthejvm.wartimizer

import quoted.*

/* 

  case class Person(name: String, age: Int, favLanguage: String, smoker: Boolean)

  before:
  person.copy(name: "Someone").copy(age = 99).copy(favLanguage = "Java")

  after:
  person.copy(name = "Someone", age = 99, favLanguage = "Java")
 */
object CopyChain extends Wartimization {
  override def treeMap(using q: Quotes): q.reflect.TreeMap = {
    import q.reflect.*
    val helper = new Helper[q.type](using q)
    import helper.*

    new TreeMap {
      override def transformTerm(tree: Term)(owner: Symbol): Term = 
        tree match {
          case CopyChainArgs(target, targetArgs, chainArgs) =>
            // figure out the latest args to apply to the final copy method
            val args = chooseLatestArgs(targetArgs, chainArgs)
            // find the copy method to invoke
            val copyMethod = getCopyMethod(target)
            // invoke the copy method on those args
            target.select(copyMethod).appliedToArgs(args).changeOwner(owner)
          case _ => 
            // base case
            super.transformTerm(tree)(owner)
        }
    }
  }

  private class Helper[Q <: Quotes](using val q: Q) {
    import q.reflect.*

    def chooseLatestArgs(targetArgs: List[Term], chainArgs: List[List[Term]]): List[Term] = 
      targetArgs.zip(chainArgs.transpose).map { // List[(Term, List[Term])]
        case (targetArg, argChain) =>
          argChain
            .findLast(arg => !arg.symbol.flags.is(Flags.Synthetic) || !arg.symbol.name.contains("copy$default$"))
            .getOrElse(targetArg)
      }

    // look up the copy method
    def getCopyMethod(target: Term): Symbol =
      target.symbol.methodMember("copy").headOption
      // should NEVER happen
        .getOrElse(report.errorAndAbort(s"The impossible happened, there is no copy method on ${target.symbol.name}", target.pos))

    object CopyChainArgs {
      def unapply(term: Term): Option[(Term, List[Term], List[List[Term]])] = 
        term match {
          // recursive: chain.copy(args)
          case Block(
            List(
              ValDef(localVal, _, Some(CopyChainArgs(target, targetArgs, innerChainArgs))), // final target of the copy call (is computed recursively)
              otherVals* // local value definitions, may be used in the last copy call
            ), // value defs
            Apply(Select(Ident(finalTarget), "copy"), args) // function application
          ) if localVal == finalTarget =>
            val localArgs = resolve(args, otherVals)
            Some((target, targetArgs, innerChainArgs :+ localArgs))
          // base case: target.copy(arguments)
          case Apply(Select(target, "copy"), targetArgs) =>
            Some((target, targetArgs, List())) // no copy chain
          // 2-chain copy: target.copy(args).copy(args2)
          case Block(
            List(intermediateVals*),
            Apply(Select(target, "copy"), targetArgs)
          ) => 
            Some((target, resolve(targetArgs, intermediateVals), List()))
          case _ => None
        }

      private def resolve(funcArgs: List[Term], valueDefs: Seq[Statement]): List[Term] = {
        // map[value name, value expression]
        val valueExpressions = valueDefs.collect {
          case ValDef(name, _, Some(expression)) => name -> expression
        }.toMap

        funcArgs.map {
          case term @ Ident(name) => valueExpressions.getOrElse(name, term)
          // `age = intermediateVal` becomes `age = 2 + 3`
          case NamedArg(argName, term @ Ident(name)) =>
            NamedArg(argName, valueExpressions.getOrElse(name, term))
          // anything else is left intact
          case term => term
        }
      }
    }
  }
}

