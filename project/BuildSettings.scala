import sbt._
import Keys._

object NexusConf {

  val nexus_ip  = "121.199.26.84"
  val nexus_url = s"http://$nexus_ip:8081/nexus"

  val publishSetting = Seq(
    publishTo := Some("nexus publish" at (nexus_url + "/content/repositories/snapshots"))
  )

  val nexusResolvers = Seq(
    credentials += Credentials(Path.userHome / ".sbt" / "credentials")
  )
}

object BuildSettings {

  val resolversSetting = NexusConf.nexusResolvers
  val publishSetting   = NexusConf.publishSetting

  val paradiseVersion = "2.1.0"

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
    organization := "com.yujieshui"

  )

  val scalaOptionSetting = scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature", "-language:_",
    "-target:jvm-1.8"
    //    "-Xlint", "-unchecked",
    //      "-Ymacro-debug-lite",
    //      "-Xexperimental",
    //      "-Ybackend:GenBCode",
    //      "-Ydelambdafy:method"
  )

  val javacOptionsSetting = javacOptions ++= Seq(
    "-encoding", "UTF-8"
  )

  val buildSettings =
    infoSetting ++ scalaOptionSetting ++ macrosSetting ++ resolversSetting ++ publishSetting ++ javacOptionsSetting

}
