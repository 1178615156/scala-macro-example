name := "scala-macro-example"

def info = Seq(
  scalaVersion := "2.11.8",
  organization := "yjs",
  libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.0",
    "org.scala-lang" % "scala-reflect" % scalaVersion.value
  ),
  version := "0.0.3"
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

def testLib = Seq(libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % Test)

def publishSetting = Seq(
  licenses += ("MIT", url("http://opensource.org/licenses/MIT"))
)

def unPublish = Seq(publishArtifact := false, publish := {})

def akkaDepend = Seq(libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.4.11" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.4.11"
))

lazy val `macros-common` = (project in file("./macros-common"))
  .settings(info ++ scalaMeta ++ testLib ++ publishSetting)

lazy val `macros-config` = (project in file("./macros-config"))
  .settings(info ++ scalaMeta ++ testLib ++ publishSetting).dependsOn(`macros-common`)

lazy val `macros-play` = (project in file("./macros-play"))
  .settings(info ++ scalaMeta ++ testLib ++ publishSetting).dependsOn(`macros-common`)

lazy val `macros-akka` = (project in file("./macros-akka"))
  .settings(info ++ scalaMeta ++ testLib ++ publishSetting ++ akkaDepend).dependsOn(`macros-common`)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(info ++ unPublish ++ scalaMeta ++ testLib ++ akkaDepend)
  .dependsOn(`macros-common`, `macros-config`, `macros-akka`, `macros-play`)

lazy val `scala-macro-example` = (project in file("."))
  .settings(info ++ scalaMeta ++ publishSetting)
  .dependsOn(`macros-common`, `macros-config`, `macros-play` ,`macros-akka` , `macros-test` % Test)
  .aggregate(`macros-common`, `macros-config`, `macros-play` ,`macros-akka` , `macros-test`)
