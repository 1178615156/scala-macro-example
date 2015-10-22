name := "scala-macro-example"

version := "1.0"

scalaVersion := "2.11.6"

val paradiseVersion = "2.1.0-M5"

crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6", "2.11.7")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)

libraryDependencies ++= (
  if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % paradiseVersion)
  else Nil
  )

lazy val macros = project.in(file("module/macros"))

lazy val root = (project in file(".")).dependsOn(macros)

scalacOptions += "-Ymacro-debug-lite"

scalacOptions += "-Xexperimental"
//
//lazy val hello_world_macro = proj("hello_world/macros")
//
//lazy val hello_world_using = proj("hello_world/using").dependsOn(hello_world_macro)