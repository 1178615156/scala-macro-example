//package slick.model
//
//import macross.slick.{SlickTU, SlickTupledAndUnapply}
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import scala.language.implicitConversions
//
///**
// * Created by YuJieShui on 2015/9/23.
// */
//object T {
//  implicit def sss(s: Future[String]): String = ""
//
//  implicit def ssss(s: String): Future[String] = Future.successful("")
//
//}
//
//import T.ssss
//
//object SlickType {
//  def tran[T](s: T): T = s
//
//  def tran(s: Future[String]): String = ""
//}
//
//case class TestEntity(
//                       age: Option[Int],
//                       name: Int,
//                       id: String,
//                       future: Future[String] = Future.successful("")
//                       )
//object TestEntity {
//  type Value = TestEntity
//
//  def slickApply = TestEntity.apply _
//}
//import TestEntity.Value
//import TestEntity.slickApply
//
//@SlickTU[TestEntity](showInfo = true)
//class TestEntitySU {
//
//}
//
//
//
//object Run extends App {
//   val a=new TestEntitySU().slickTupled(None,1,"1","sss")
//    println(a)
//  println(new TestEntitySU().slickUnapply(a))
//}