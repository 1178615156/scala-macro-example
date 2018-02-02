name := "macros-utils"

def macroAnnotationSettings = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in(Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
)

def options = Seq(
  javacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-feature", "-language:_")
  , scalacOptions ++= Seq("-Xlint", "-unchecked")
  , scalacOptions ++= Seq("-deprecation")
)

def commonSetting = macroAnnotationSettings ++ options ++ Seq(
  scalaVersion := "2.12.4",
  organization := "yjs",
  version := "0.0.4",
  libraryDependencies ++= Libs.config ++ Libs.test ++ Libs.slf4j.slf4j,
  libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
)

def publishSetting = Seq()

def slickDepend = Seq(libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.0")

def unPublish = Seq(publishArtifact := false, publish := {})

lazy val `macros-common` = (project in file("./macros-common"))
  .settings(commonSetting)

lazy val `macros-utensil` = (project in file("./macros-utensil"))
  .settings(commonSetting)
  .dependsOn(`macros-common`)

lazy val `macros-play` = (project in file("./macros-play"))
  .settings(commonSetting)
  .dependsOn(`macros-common`)

lazy val `macros-slick` = (project in file("./macros-slick"))
  .settings(commonSetting)
  .settings(Seq(libraryDependencies ++= Libs.slick))
  .dependsOn(`macros-common`)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(commonSetting)
  .settings(Seq(libraryDependencies ++= Libs.logback))
  .dependsOn(`macros-common`, `macros-utensil`, `macros-play`, `macros-slick`)

lazy val `scala-macro-example` = (project in file("."))
  .settings(commonSetting)
  .dependsOn(`macros-common`, `macros-utensil`, `macros-play`, `macros-slick`, `macros-test` % Test)
  .aggregate(`macros-common`, `macros-utensil`, `macros-play`, `macros-slick`, `macros-test`)
