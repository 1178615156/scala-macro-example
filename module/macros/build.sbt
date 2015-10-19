name := "macross"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

organization := "com.paichufang"

val paradiseVersion = "2.1.0-M5"

crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6", "2.11.7")

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

val nexus_url = "http://192.168.1.200:8081/nexus/"

resolvers += "Nexus" at nexus_url + "content/groups/public"

credentials += Credentials("Sonatype Nexus Repository Manager",
  "192.168.1.200", "admin", "admin123")

publishTo := Some("releases" at (nexus_url + "content/repositories/snapshots"))
