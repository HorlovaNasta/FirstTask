name := "GlobalPowerPlans"

version := "0.1"

scalaVersion := "2.12.8"
libraryDependencies += "com.github.tototoshi" %% "scala-csv" % "1.3.5"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe" % "config" % "1.3.3"

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")