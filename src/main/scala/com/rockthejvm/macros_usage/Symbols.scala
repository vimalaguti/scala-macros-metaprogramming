package com.rockthejvm.macros_usage

import com.rockthejvm.macros.Symbols.*
import scala.annotation.nowarn

object Symbols {
  
  @nowarn("msg=deprecated")
  enum Permissions {
    case Read(bitset: Int, dir: String, mask: Boolean)
    case Denied

    private def changePermissions(b: Int, dir: String) = s"dir $dir just changed permissions to $b"

    val bitMask = 0xFF
  }

  describeSymbols[Permissions]


}
