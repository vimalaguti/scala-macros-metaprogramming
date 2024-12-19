package com.rockthejvm.macros

import quoted.*

object DefDefs {
  inline def generateDynamicFunction(anInt: Int, aString: String, aBoolean: Boolean) =
    ${ generateDynamicFunctionImpl('anInt, 'aString, 'aBoolean) }

  def generateDynamicFunctionImpl(theInt: Expr[Int], theString: Expr[String], theBoolean: Expr[Boolean])(using q: Quotes) = {
    import q.reflect.*

    // method signature
    val defSymbol = Symbol.newMethod(
      parent = Symbol.spliceOwner, // the scope where you want to add it
      name = "myFunction",
      tpe = PolyType(
        paramNames = List("A")
      )(
        paramBoundsExp = _ =>
          List(
            TypeBounds.upper(TypeRepr.of[Int]) // type bounds for the first argument
          ),
        resultTypeExp = typeParams =>
          MethodType( // type of the method is its entire signature
            paramNames = List("anInt", "aString", "aBoolean") // names of all arguments
          )(
            paramInfosExp = _ => List(typeParams.param(0), TypeRepr.of[String], TypeRepr.of[Boolean]), // argument types
            resultTypeExp = _ => TypeRepr.of[Int] // return type
          ),
      ),
      flags = Flags.EmptyFlags, // all the modifiers of the method definition
      privateWithin = Symbol.noSymbol
    )

    // method body
    def defBody(args: List[List[Tree]]): Option[Term] = Some {
      // given Quotes = defSymbol.asQuotes // needed to make this term "owned" by the definition

      val List(listOfTypes, List(intTerm, stringTem, booleanTerm)) = args

      val theInt = intTerm.asExprOf[Int]
      val theString = stringTem.asExprOf[String]
      val theBoolean = booleanTerm.asExprOf[Boolean]

      '{ if ($theBoolean) $theInt else $theString.length  }
        .asTerm
        .changeOwner(defSymbol) // same as with the given Quotes
    }

    // method definition = signature + implementation
    val defDef = DefDef(defSymbol, defBody)

    // using this method needs to REFER to it
    val defRef = Ref(defSymbol)
    // invoke the Ref => returns a Term
    val defUsage = defRef
      .appliedToTypes(List(TypeRepr.of[Int]))
      .appliedTo(theInt.asTerm, theString.asTerm, theBoolean.asTerm)
    
    Block(
      List(defDef),
      defUsage
    ).asExprOf[Int]
  }
}
