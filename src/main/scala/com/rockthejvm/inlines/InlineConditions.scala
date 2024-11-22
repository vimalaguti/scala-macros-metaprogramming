package com.rockthejvm.inlines

object InlineConditions {
  
  inline def condition(b: Boolean): String = 
    inline if (b) "yes"
    else "no"


  val positive = condition(true) // known to be "yes" at COMPILE TIME
  /* 
    =>
    inline if (true) "yes"
    else "no"

    => "yes"
   */

  // compiler can do basic computation
  val positive_v2 = condition(true && !false) // also known to be "yes"

  val variable = true
  // val question = condition(variable) // does not compile because the variable is not known

  transparent inline def conditionUnion(b: Boolean): String | Int = 
    inline if (b) "yes"
    else 0

  val aString = conditionUnion(true) // known to be String
  val anInt = conditionUnion(true && false) // known to be Int

  // inline matches
  inline def matcher(x: Int) = inline x match {
    case 1 => "one"
    case 2 => "two"
    case 3 => "three"
  }

  val theOne = matcher(1) // known to be "one"
  // val nothing = matcher(99) // will not compile unless there's a pattern to match this

  transparent inline def matcherT(x: Int): String | Int = inline x match {
    case 1 => 1
    case 2 => "two"
    case 3 => "three"
    case _ => 0
  }

  val theOneInt = matcherT(1)

  transparent inline def matchOption(x: Option[Any]): String = 
    inline x match {
      case Some(value: String) => value
      case Some(value: Int) => value.toString
      case None => "nothing"
    }

  val something = matchOption(Some("something")) // known to be the value of the Some("something")
  // val aBoolean = matchOption(Some(true)) does not compile (exhaustiveness)

  val anOption = Option("my perfect string")
  // val myPerfectString = matchOption(anOption) // won't compile (too general type)
}
