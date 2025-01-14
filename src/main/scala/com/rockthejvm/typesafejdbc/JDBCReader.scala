package com.rockthejvm.typesafejdbc

trait JDBCReader[A] {
  def read(value: Any): A
}
