import sbt._
import Keys._


object BuildProject extends Build {

  import BuildSettings._
  import LibDepend._

  val unPublish = publishArtifact := false

  lazy val macros = Project("macros", file("macros"), settings =
    buildSettings ++ testFrameworkDepend ++ slickDepend ++ akkaDepend
  )

  lazy val stackoverflow = Project("stackoverflow", file("stackoverflow"), settings =
    buildSettings ++ unPublish) dependsOn macros

  lazy val using = Project("macros_using", file("macros_using"), settings =
    buildSettings ++ unPublish ++ slickDepend ++ testFrameworkDepend
  ) dependsOn macros dependsOn stackoverflow

  lazy val `scala-macro-example` = Project("scala-macro-example", file("."), settings =
    buildSettings ++ unPublish ++ Seq()
  )
    .dependsOn(
      macros % "compile->compile;test->test",
      using % "compile->compile;test->test",
      stackoverflow % "test->test")
    .aggregate(macros, using, stackoverflow)
}


//scalacOptions += "-Ymacro-debug-lite"
//
//scalacOptions += "-Xexperimental"
