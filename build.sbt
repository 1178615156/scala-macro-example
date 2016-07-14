name := "scala-macro-example"

def info = Seq(
  version := "2.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  organization := "yjs"
)

def scalaMeta = Seq(
  addCompilerPlugin("org.scalamacros" % "paradise" % "3.0.0-M3" cross CrossVersion.full),
  libraryDependencies += "org.scalameta" %% "scalameta" % "1.0.0"
)

def testLib = Seq(
  libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % Test
)

def unPublish = {
  Seq(
    publishArtifact := false,
    publish := {}
  )
}

def publishSetting = {
  val nexus_ip = "121.199.26.84"
  val nexus_url = s"http://$nexus_ip:8081/nexus/"
  Seq(
    credentials += Credentials(Path.userHome / ".sbt" / "credentials")
    , publishTo := Some("snapshots" at nexus_url + "content/repositories/snapshots")
  )
}

lazy val `macros-config` = (project in file("./macros-config"))
  .settings(info ++ publishSetting ++ scalaMeta ++ testLib)

lazy val `macros-test` = (project in file("./macros-test"))
  .settings(info ++ unPublish ++ scalaMeta ++ testLib)
  .dependsOn(`macros-config`)

lazy val root = (project in file("."))
  .settings(info ++ unPublish ++ scalaMeta)
  .dependsOn(`macros-config`).aggregate(`macros-config`)
  .dependsOn(`macros-test`).aggregate(`macros-test`)