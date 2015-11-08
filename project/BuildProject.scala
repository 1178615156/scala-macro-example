import sbt._
import Keys._


object BuildProject extends Build {

  import BuildSettings._
  import LibDepend._

  lazy val macros = Project("macros", file("macros"), settings =
    buildSettings ++ testFrameworkDepend ++ publishSetting
  )

  lazy val macros_try = Project("macros_try", file("macros_try"),
    settings = buildSettings ++ slickDepend) dependsOn macros

  lazy val stackoverflow = Project("stackoverflow", file("stackoverflow"), settings =
    buildSettings) dependsOn macros

  lazy val using = Project("macros_using", file("macros_using"), settings =
    buildSettings) dependsOn macros dependsOn macros_try dependsOn stackoverflow

  lazy val `scala-macro-example` = Project("scala-macro-example", file("."), settings =
    buildSettings).aggregate(macros, using, macros_try, stackoverflow)
}


//scalacOptions += "-Ymacro-debug-lite"
//
//scalacOptions += "-Xexperimental"
