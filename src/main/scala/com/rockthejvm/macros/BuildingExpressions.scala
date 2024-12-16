package com.rockthejvm.macros

import quoted.*

object BuildingExpressions {
  enum Permissions {
    case Denied
    case Bitset(value: Int)
    case Custom(dirs: List[String])
  }

  def buildStringExpr(using Quotes): Expr[String] =
    Expr("this is a string expression") // ToExpr[String] exists, same for Int, ...

  inline def createDefaultPermissions(): Permissions =
    ${ buildPermissionExpr }

  // doesn't compile UNLESS we have a given ToExpr[Permissions]
  def buildPermissionExpr(using Quotes): Expr[Permissions] =
    Expr(Permissions.Custom(List("photos", "books"))) // requires a given ToExpr[Permissions]

  // given ToExpr[CustomType]
  import Permissions.*
  given ToExpr[Permissions] with 
    def apply(value: Permissions)(using Quotes): Expr[Permissions] = 
      value match {
        case Denied => '{ Denied } 
        case Bitset(value) => 
          val valueExpr = Expr(value) // this is possible because we have ToExpr[Int]
          '{ Bitset($valueExpr) }
        case Custom(dirs) => 
          val dirsExpr = Expr(dirs) // this is possible for lists
          '{ Custom($dirsExpr) }
      }

  // given FromExpr[CustomType]
  given FromExpr[Permissions] with 
    def unapply(x: Expr[Permissions])(using Quotes): Option[Permissions] = 
      x match {
        case '{ Denied } => Some(Denied)
        case '{ Bitset($value) } =>
          val theValue = value.valueOrAbort // can surface compile errors
          Some(Bitset(theValue))
        case '{ Custom($list) } => // list = Expr[List[String]]
          val theDirs = list.valueOrAbort
          Some(Custom(theDirs))
        case _ => None
      }

  inline def describePermissions(inline permissions: Permissions) =
    ${ describePermissionsImpl('permissions) }

  def describePermissionsImpl(permissions: Expr[Permissions])(using Quotes): Expr[String] = {
    val p = permissions.valueOrAbort // can turn an Expr into a value if I have a given FromExpr[ThatType] in scope

    val result = p match {
      case Denied => "no permissions"
      case Bitset(value) => s"limited general permissions: $value"
      case Custom(dirs) => s"wide permissions for a limited dir set: ${dirs}"
    }

    Expr(result)
  }
}
