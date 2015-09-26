package slick.model

import macross.slick.SlickTupledAndUnapply

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

/**
 * Created by YuJieShui on 2015/9/23.
 */
object T {
  implicit def sss(s: Future[String]): String = ""

  implicit def ssss(s: String): Future[String] = Future.successful("")

}

import T.ssss

object SlickType {
  def tran[T](s: T): T = s

  def tran(s: Future[String]): String = ""
}

@SlickTupledAndUnapply(showInfo = false)
case class TestEntity(
                       age: Option[Int],
                       name: Int,
                       id: String,
                       future: Future[String] = Future.successful("")
                       )

object TestEntity {
}
