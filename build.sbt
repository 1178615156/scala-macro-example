name := "scala-macro-example"

def info = Seq(
  scalaVersion := "2.11.8",
  organization := "yjs",
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % scalaVersion.value,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang" % "scala-compiler" % scalaVersion.value,
    "com.typesafe" % "config" % "1.3.0"
  )
)

def scalaMeta = Seq(
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M5" cross CrossVersion.full),
  libraryDependencies ++= Seq("org.scalameta" %% "scalameta" % "1.2.0").map(excludeScalaLib).map(_.withSources())
)

def excludeScalaLib(l: ModuleID) = l
  .exclude("org.scala-lang", "scala-library")
  .exclude("org.scala-lang", "scala-reflect")
  .exclude("org.scala-lang", "scala-compiler")

def options = Seq(
  javacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-feature", "-language:_")
  , scalacOptions ++= Seq("-Xlint", "-unchecked")
  , scalacOptions ++= Seq("-deprecation")
)

def logDepend = Seq(libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.12")

def testLib = Seq(libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % Test)

def unPublish = Seq(publishArtifact := false, publish := {})

def measureDepend = Seq(libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.7")

def akkaDepend = Seq(libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.4.11" % "test",
  "com.typesafe.akka" %% "akka-actor" % "2.4.11"
))

lazy val `macros-common` = (project in file("./macros-common"))
  .settings(info ++ scalaMeta ++ testLib)

lazy val `macros-config` = (project in file("./macros-config"))
  .settings(info ++ scalaMeta ++ testLib).dependsOn(`macros-common`)

lazy val `macros-play` = (project in file("./macros-play"))
  .settings(info ++ scalaMeta ++ testLib).dependsOn(`macros-common`)

lazy val `macros-akka` = (project in file("./macros-akka"))
  .settings(info ++ scalaMeta ++ testLib ++ akkaDepend).dependsOn(`macros-common`)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(info ++ unPublish ++ scalaMeta ++ testLib ++ akkaDepend)
  .dependsOn(`macros-common`, `macros-config`, `macros-akka`, `macros-play`)

lazy val `scala-macro-example` = (project in file("."))
  .settings(info ++ unPublish ++ scalaMeta)
  .dependsOn(`macros-common`, `macros-config`, `macros-play`, `macros-test`)
  .aggregate(`macros-common`, `macros-config`, `macros-play`, `macros-test`)
