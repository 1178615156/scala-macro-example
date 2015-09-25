package slick.model

import macross.slick.SlickTupled

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Created by YuJieShui on 2015/9/23.
 */
@SlickTupled(showInfo = false)
case class TestEntity(
                       age: Option[Int],
                       name: Int,
                       id: String,
                       future: Future[String] = Future.successful("")
                       )

object TestEntity {
}
