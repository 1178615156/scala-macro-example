import sbt._
import Keys._
//import bintray._

object BuildProject extends Build {

  import BuildSettings._
  import LibDepend._
  val configLib = libraryDependencies +="com.typesafe" % "config" % "1.3.0"
  val unPublish = publishArtifact := false

  lazy val macrosConfig = Project("macros-config",file("macros-config"),
    settings=buildSettings++testFrameworkDepend ++ configLib)

  lazy val macros = Project("macros", file("macros"), settings =
    buildSettings ++ testFrameworkDepend ++ slickDepend ++ akkaDepend
  )

  lazy val so = Project("so", file("so"), settings =
    buildSettings ++ unPublish) dependsOn macros

  lazy val using = Project("macros_using", file("macros_using"), settings =
    buildSettings ++ unPublish ++ slickDepend ++ testFrameworkDepend
  ) .dependsOn(macros,macrosConfig, so)

  lazy val `scala-macro-example` = Project("scala-macro-example", file("."), settings =
    buildSettings ++ unPublish ++ Seq()
  )
    .dependsOn(macros,macrosConfig, using, so)
    .aggregate(macros,macrosConfig, using, so)
}


//scalacOptions += "-Ymacro-debug-lite"
//
//scalacOptions += "-Xexperimental"
