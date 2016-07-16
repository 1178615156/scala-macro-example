name := "scala-macro-example"

def info = Seq(
  version := "2.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  organization := "yjs"
)


def scalaMeta = Seq(
  resolvers += Resolver sonatypeRepo "snapshots",
  addCompilerPlugin("org.scalamacros" % "paradise" % "3.0.0-SNAPSHOT" cross CrossVersion.full),
  libraryDependencies += "org.scalameta" %% "scalameta" % "1.1.0-SNAPSHOT"
)
def options = Seq(
  javacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-encoding", "UTF-8")
  , scalacOptions ++= Seq("-feature", "-language:_")
  , scalacOptions ++= Seq("-Xlint", "-unchecked")
  , scalacOptions ++= Seq("-deprecation")
  //  , scalacOptions ++= Seq("-target:jvm-1.8")
  //scalacOptions ++= List("-Ybackend:GenBCode", "-Ydelambdafy:method", "-target:jvm-1.8")
)

def testLib = Seq(libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % Test)

def credentialSetting = Seq(credentials += Credentials(Path.userHome / ".sbt" / "credentials"))

def unPublish = Seq(publishArtifact := false, publish := {})

def publishSetting = {
  val nexus_ip = "121.199.26.84"
  val nexus_url = s"http://$nexus_ip:8081/nexus/"
  Seq(
    publishTo := Some("snapshots" at nexus_url + "content/repositories/snapshots")
  ) ++ credentialSetting
}



lazy val `macros-config` = (project in file("./macros-config"))
  .settings(info ++ publishSetting ++ scalaMeta ++ testLib)

lazy val `macros-play` = (project in file("./macros-play"))
  .settings(info ++ publishSetting ++ scalaMeta ++ testLib)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(info ++ unPublish ++ scalaMeta ++ testLib ++ credentialSetting)
  .dependsOn(`macros-config`)
  .dependsOn(`macros-play`)

lazy val root = (project in file("."))
  .settings(info ++ unPublish ++ scalaMeta ++ credentialSetting)
  .dependsOn(`macros-config`).aggregate(`macros-config`)
  .dependsOn(`macros-test`).aggregate(`macros-test`)
  .dependsOn(`macros-play`).aggregate(`macros-play`)
