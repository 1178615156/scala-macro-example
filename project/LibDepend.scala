import sbt._
import Keys._


object LibDepend {

  val slickLib    = Seq("com.typesafe.slick" %% "slick" % "3.0.0")
  val slickDepend = libraryDependencies ++= slickLib

  val testFrameworkLib    = Seq(
    "com.google.guava" % "guava" % "18.0" % "test",
    ("org.testng" % "testng" % "6.9.6" % "test").exclude(
      "com.google.guava", "guava"
    ),
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
  )
  val testFrameworkDepend = libraryDependencies ++= testFrameworkLib
  val akkaDepend          = libraryDependencies ++= Seq(
    "com.typesafe.akka" % "akka-actor_2.11" % "2.3.12"
    //    "com.typesafe.akka" % "akka-kernel_2.11" % "2.3.12",
    //    "com.typesafe.akka" % "akka-remote_2.11" % "2.3.12"
  )

}