import sbt._
import Keys._

object BuildSettings {


  val nexus_ip = "localhost"
  val nexus_url = s"http://$nexus_ip:8081/nexus/"
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

  val resolversSetting: Seq[Def.Setting[Seq[Resolver]]] = Seq(
    resolvers += "Nexus" at nexus_url + "content/groups/public",
    resolvers += Resolver.url("Edulify Repository", url(s"http://$nexus_ip:8081/nexus/content/groups/public"))(Resolver.ivyStylePatterns)
  )

  val publishSetting = Seq(
    credentials += Credentials("Sonatype Nexus Repository Manager", nexus_ip, "admin", "admin123"),
    publishTo := Some("releases" at (nexus_url + "content/repositories/snapshots"))
  )

  val buildSettings = Seq(
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
    //    libraryDependencies += "org.scala-lang.modules" %% "scala-java8-compat" % "0.5.0"
  ) ++ macroSetting ++ resolversSetting ++ publishSetting

}
