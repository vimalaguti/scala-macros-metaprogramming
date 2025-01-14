ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.6.2"

ThisBuild / scalacOptions ++= Seq(
  "-Xprint:postInlining",
  "-Xmax-inlines:100000"
)

lazy val root = (project in file("."))
  .settings(
    name := "scala-black-magic",
    libraryDependencies ++= Seq(
      "org.postgresql"         % "postgresql"                        % "42.7.1",
    )
  )
