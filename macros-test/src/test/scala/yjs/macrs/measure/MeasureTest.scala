//package yjs.macrs.measure
//
//import org.slf4j.Logger
//import yjs.macrs.measure.Measure.Record
//
///**
//  * Created by yujieshui on 2016/8/16.
//  */
//class MeasureTest extends org.scalatest.FunSuite {
//
//  implicit def log[T]: Record[T] = new Record[T] {
//    override def apply(t: => T, method: String): T = {
//      println(method)
//      t
//    }
//  }
//
//  @Measure
//  def hello_world(): Unit = {
//    println("xxxx")
//  }
//
//  test("x") {
//    hello_world()
//  }
//}
