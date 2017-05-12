name := "macros-utils"

def info = Seq(
  scalaVersion := "2.11.8",
  organization := "yjs",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.0",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  ),
  version := "0.0.4"
)

def scalaMeta = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M5" cross CrossVersion.full),
  libraryDependencies ++= Seq("org.scalameta" %% "scalameta" % "1.2.0")
)

def options = Seq(
  javacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-feature", "-language:_")
  , scalacOptions ++= Seq("-Xlint", "-unchecked")
  , scalacOptions ++= Seq("-deprecation")
)

def logDepend = Seq(libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.12")

def testLib = Seq(libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % Test)

def publishSetting = Seq()

def slickDepend = Seq(libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.0")

def unPublish = Seq(publishArtifact := false, publish := {})

lazy val `macros-common` = (project in file("./macros-common"))
  .settings(info ++ scalaMeta ++ testLib ++ publishSetting)

lazy val `macros-config` = (project in file("./macros-config"))
  .settings(info ++ scalaMeta ++ testLib ++ publishSetting).dependsOn(`macros-common`)

lazy val `macros-play` = (project in file("./macros-play"))
  .settings(info ++ scalaMeta ++ testLib ++ publishSetting).dependsOn(`macros-common`)

lazy val `macros-slick` = (project in file("./macros-slick"))
  .settings(info ++ testLib ++ publishSetting ++ slickDepend).dependsOn(`macros-common`)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(info ++ unPublish ++ scalaMeta ++ testLib)
  .dependsOn(`macros-common`, `macros-config`, `macros-play`, `macros-slick`)

lazy val `scala-macro-example` = (project in file("."))
  .settings(info ++ scalaMeta ++ publishSetting)
  .dependsOn(`macros-common`, `macros-config`, `macros-play`, `macros-slick`, `macros-test` % Test)
  .aggregate(`macros-common`, `macros-config`, `macros-play`, `macros-slick`, `macros-test`)
