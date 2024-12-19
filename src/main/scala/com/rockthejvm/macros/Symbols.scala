package com.rockthejvm.macros

import scala.quoted.*

object Symbols {
  
  inline def describeSymbols[A]: Unit = 
    ${ describeSymbolsImpl[A] }

  def describeSymbolsImpl[A: Type](using q: Quotes): Expr[Unit] = {
    import q.reflect.*

    val typeA = TypeRepr.of[A]
    // symbol = describes the "identifier" of the type representation
    val typeSymbol = typeA.typeSymbol
    val firstMethod = typeSymbol.methodMember("changePermissions").head
    val bitMaskField =  typeSymbol.fieldMember("bitMask")

    val descriptions = List(
      // fully qualified name
      typeSymbol.fullName,
      // companion
      typeSymbol.companionModule,

      // find method members
      firstMethod.paramSymss, // list of all param lists, expressed as symbols
      //         ^ returns a list, because methods can be overloaded

      // modifiers of this definition (private, final, inline ...)
      firstMethod.flags.show, 

      // check if a symbol is a certain kind of definition
      firstMethod.isDefDef, // check if this def is a method (def)

      // cehck the position of this def in the source code
      bitMaskField.pos, // useful especially for error reporting

      // class hierarchy or inspection of subtypes
      typeSymbol
        .children(0) // can inspect child types
        .primaryConstructor // can check constructors
        .paramSymss,

      typeSymbol.children(0).caseFields, // useful for cases of enums, and for case classes

      // can inspect annotations
      typeSymbol.getAnnotation(Symbol.classSymbol("scala.annotation.nowarn")),

      // check the "owner" of this symbol
      typeSymbol.owner, // also a Symbol
      // ... and you can inspect it further
      
      typeSymbol.owner.owner // also a Symbol
    )

    println("=====================================")
    descriptions.foreach(println)
    println("=====================================")

    '{ () }
  }
}
