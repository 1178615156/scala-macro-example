name := "scala-macro-example"

def info = Seq(
  version := "2.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  organization := "yjs",
  credentials += Credentials(Path.userHome / ".sbt" / "credentials")
)

def scalaMacroAndMeta = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "3.0.0-M3" cross CrossVersion.full),
  libraryDependencies += "org.scalameta" %% "scalameta" % "1.0.0",
  libraryDependencies <+= (scalaVersion) ("org.scala-lang" % "scala-reflect" % _)
)

def options = Seq(
  javacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-feature", "-language:_")
  , scalacOptions ++= Seq("-Xlint", "-unchecked")
  , scalacOptions ++= Seq("-deprecation")
)

def testLib = Seq(libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % Test)

def unPublish = Seq(publishArtifact := false, publish := {})


lazy val `so` = project in file("./so") settings (info ++ unPublish ++ scalaMacroAndMeta)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(info ++ unPublish ++ scalaMacroAndMeta ++ testLib)
  .dependsOn(`so`)

lazy val root = (project in file("."))
  .settings(info ++ unPublish ++ scalaMacroAndMeta)
  .dependsOn(`so`).aggregate(`so`)
  .dependsOn(`macros-test`).aggregate(`macros-test`)
