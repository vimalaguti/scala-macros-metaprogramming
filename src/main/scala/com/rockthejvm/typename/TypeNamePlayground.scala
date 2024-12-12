package com.rockthejvm.typename

import scala.util.Try

object TypeNamePlayground {
  def main(args: Array[String]) = {
    val typeName = TypeName[List[Option[Try[String]]]]
    println(typeName)
  }
}
