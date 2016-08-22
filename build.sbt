name := "scala-macro-example"

def info = Seq(
  version := "2.3.1",
  scalaVersion := "2.11.8",
  organization := "yjs",
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-library" % scalaVersion.value,
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-lang" % "scala-compiler" % scalaVersion.value
  )
)

def scalaMeta = Seq(
  resolvers += Resolver sonatypeRepo "snapshots",
  addCompilerPlugin("org.scalamacros" % "paradise" % "3.0.0-M3" cross CrossVersion.full),
  libraryDependencies ++= Seq("org.scalameta" %% "scalameta" % "1.0.0").map(excludeScalaLib).map(_.withSources())
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
  //  , scalacOptions ++= Seq("-target:jvm-1.8")
  //    , scalacOptions ++= Seq("-target:jvm-1.8")
  //scalacOptions ++= List("-Ybackend:GenBCode", "-Ydelambdafy:method", "-target:jvm-1.8")
)

def measure = Seq(libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.7")

def logDepend = Seq(libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.12")

def testLib = Seq(libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % Test)

def unPublish = Seq(publishArtifact := false, publish := {})

def measureDepend = Seq(libraryDependencies += "com.storm-enroute" %% "scalameter" % "0.7")

lazy val `macros-config` = (project in file("./macros-config"))
  .settings(info ++ scalaMeta ++ testLib ++ Seq(libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.0"
  )))

lazy val `macros-play` = (project in file("./macros-play"))
  .settings(info ++ scalaMeta ++ testLib ++ Seq(libraryDependencies ++= Seq(
    "com.typesafe" % "config" % "1.3.0"
  )))

lazy val `macros-measure` = (project in file("./macros-measure"))
  .settings(info ++ scalaMeta ++ testLib ++ measureDepend ++ logDepend)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(info ++ unPublish ++ scalaMeta ++ testLib)
  .dependsOn(`macros-config`, `macros-measure`, `macros-play`)

lazy val root = (project in file("."))
  .settings(info ++ unPublish ++ scalaMeta)
  .dependsOn(`macros-config`, `macros-play`, `macros-measure`, `macros-test`)
  .aggregate(`macros-config`, `macros-play`, `macros-measure`, `macros-test`)
