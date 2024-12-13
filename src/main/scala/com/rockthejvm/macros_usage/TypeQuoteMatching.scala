package com.rockthejvm.macros_usage

import com.rockthejvm.macros.TypeQuoteMatching.*
import scala.util.Try

object TypeQuoteMatching {
  val intDescriptor = matchType[Int]
  val listIntDescriptor = matchType[List[Int]]
  val stringListDescriptor = matchType[List[String]]
  val eitherDescriptor = matchType[Either[Throwable, Int]]
  val functionDescriptor = matchType[Int => String]
  val tupleDescriptor = matchType[(Int, String, Int)]
  val tryDescriptor = matchType[Try[Int]]
  val tryDescriptor2 = matchType[Try[String]]
}
