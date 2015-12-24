import sbt._
import Keys._

object BuildSettings {

  import NexusConf._

  val paradiseVersion = "2.1.0-M5"

  val macroSetting = Seq(
    crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6", "2.11.7"),
    addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
    libraryDependencies <+= (scalaVersion) ("org.scala-lang" % "scala-reflect" % _),
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % paradiseVersion)
      else Nil
      )
  )

  val resolversSetting = nexusResolvers.getOrElse(Nil)
  val publishSetting   = nexusResolvers.getOrElse(Nil)
  val buildSettings    = Seq(
    organization := "com.yjs",
    scalaVersion := "2.11.7",
    version := "1.0-SNAPSHOT",
    scalacOptions ++= Seq(
      //      "-Ymacro-debug-lite",
      //      "-Xexperimental",
      //      "-target:jvm-1.8",
      "-encoding", "UTF-8"
      //      "-Ybackend:GenBCode",
      //      "-Ydelambdafy:method"
    )
  ) ++ macroSetting ++ resolversSetting ++ publishSetting

}
