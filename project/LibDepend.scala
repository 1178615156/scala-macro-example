import sbt._
import Keys._


object LibDepend {

  val slickLib =Seq( "com.typesafe.slick" %% "slick" % "3.0.0")
  val slickDepend = libraryDependencies ++= slickLib

  val testFrameworkLib = Seq("org.testng" % "testng" % "6.9.6" % "test")
  val testFrameworkDepend = libraryDependencies ++= testFrameworkLib

}