package com.rockthejvm.typesafejdbc

import java.sql.*
import scala.collection.mutable.ListBuffer

object JDBCCommunication {

  private def withConnection[A](f: Connection => A): A = {
    // load the driver
    Class.forName("org.postgresql.Driver") // necessary to load the driver during macro expansion

    // get a connection to the DB
    val connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "docker", "docker")

    // create a PreparedStatement
    try {
      f(connection)
    } finally {
      connection.close() // prevent resource leaks
    } 
  }

  private def parseRows(schema: Schema, resultSet: ResultSet): List[Row] = {
    val result = ListBuffer.empty[Row]
    while (resultSet.next()) {
      // at this point the resultSet is "looking at" a row
      val row = schema.values.map { descriptor =>
        val value = resultSet.getObject(descriptor.index) match { // take out the value at column index i
          case array: java.sql.Array => array.getArray()
          case obj => obj
        }

        descriptor.name -> value
      }

      result += Row(row.toMap)
    }

    result.toList
  }

  def getSchema(query: String): Schema = 
    withConnection { connection =>
      val statement = connection.prepareStatement(query)

      // get metadata out of that PreparedStatement
      val metadata = statement.getMetaData()

      // => Schema
      Schema.fromMetadata(metadata)
    }

  def runQuery(query: String): List[Row] = 
    withConnection { connection => 
      val statement = connection.createStatement()
      val result = statement.executeQuery(query)
      val metadata = result.getMetaData()
      val schema = Schema.fromMetadata(metadata)
      parseRows(schema, result)
    }
}
