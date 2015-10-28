import sbt._
import Keys._

object BuildSettings {

  val nexus_url = "http://192.168.1.200:8081/nexus/"
  val paradiseVersion = "2.1.0-M5"

  val macroSetting = Seq(
    crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6", "2.11.7"),
    resolvers += Resolver.sonatypeRepo("snapshots"),
    resolvers += Resolver.sonatypeRepo("releases"),
    addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full),
    libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _),
    libraryDependencies ++= (
      if (scalaVersion.value.startsWith("2.10")) List("org.scalamacros" %% "quasiquotes" % paradiseVersion)
      else Nil
      )
  )

  val publishSetting = Seq(
    resolvers += "Nexus" at nexus_url + "content/groups/public",
    credentials += Credentials("Sonatype Nexus Repository Manager", "192.168.1.200", "admin", "admin123"),
    publishTo := Some("releases" at (nexus_url + "content/repositories/snapshots"))
  )

  val buildSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := "2.11.6",
    version := "1.0-SNAPSHOT"
  ) ++ macroSetting ++ publishSetting

}

object P extends Build {

  import BuildSettings._

  lazy val macros = Project("macros", file("macros"), settings = buildSettings ++
    Seq(
      organization := "com.yjs"
    )
  )


  lazy val macros_try = Project("macros_try", file("macros_try"),
    settings = buildSettings ++ Seq(publishArtifact := false)) dependsOn macros

  lazy val stackoverflow = Project("stackoverflow", file("stackoverflow"), settings =
    buildSettings ++ Seq(publishArtifact := false)) dependsOn macros

  lazy val using = Project("macros_using", file("macros_using"), settings =
    buildSettings ++ Seq(publishArtifact := false)) dependsOn macros dependsOn macros_try dependsOn stackoverflow

  lazy val `scala-macro-example` = Project("scala-macro-example", file("."), settings = buildSettings ++ Seq(
    organization := "com.yjs",
    publishArtifact := false
  )).aggregate(macros, using, macros_try, stackoverflow)
}


//scalacOptions += "-Ymacro-debug-lite"
//
//scalacOptions += "-Xexperimental"
