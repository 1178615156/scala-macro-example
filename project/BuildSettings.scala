import sbt._
import Keys._

object NexusConf {

  val nexus_ip  = "localhost"
  val nexus_url = (s"http://${nexus_ip}:8081/nexus/")


  val publishSetting = Seq(
    credentials += Credentials("Sonatype Nexus Repository Manager",
      nexus_ip,
      ("nexus.name"),
      ("nexus.password")
    ),
    publishTo := Some("releases" at (nexus_url + "content/repositories/snapshots"))
  )

  val nexusResolvers = Seq(
    credentials += Credentials("Sonatype Nexus Repository Manager",
      nexus_ip,
      ("nexus.name"),
      ("nexus.password")
    ),
    resolvers += "localhost" at s"$nexus_url/content/groups/public/"
  )

}

object BuildSettings {

  import NexusConf._

  val resolversSetting = Nil
  val publishSetting   = Nil

  val paradiseVersion = "2.1.0-M5"

  val macrosSetting = Seq(
    crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6"),
    addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
    libraryDependencies <+= (scalaVersion) ("org.scala-lang" % "scala-reflect" % _),
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % paradiseVersion)
      else Nil
      )
  )


  val infoSetting = Seq(
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.11.6",
    organization := "com.yjs"
  )

  val scalaOptionSetting = scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    //    "-feature", "-language:_",
    //    "-Xlint", "-unchecked",
    "-target:jvm-1.8"
  )
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
  ) ++ macrosSetting ++ resolversSetting ++ publishSetting

}
