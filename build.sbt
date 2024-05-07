ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.1"

lazy val root = (project in file("."))
  .settings(
    name := "REPS_FWJ_sbt"
  )

libraryDependencies += "org.playframework" %% "play-json" % "3.0.3"

libraryDependencies ++= Seq(
  "org.jfree" % "jfreechart" % "1.5.4",
  "org.jfree" % "jcommon" % "1.0.24"
)

libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"

