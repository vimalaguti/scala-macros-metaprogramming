package com.rockthejvm.typesafejdbc

object JDBCNullability {
  // value-level: add these cases in Schema instances
  enum VL {
    case Nullable, NonNullable
  }

  sealed trait TL
  object TL {
    sealed trait Nullable extends JDBCNullability.TL
    sealed trait NonNullable extends JDBCNullability.TL
  }
}