package slick.model

import macross.slick.SlickTupled

import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by YuJieShui on 2015/9/23.
 */
@SlickTupled(showInfo = false)
case class TestEntity(
                       age: Option[Int],
                       name: Int,
                       id: String
                       )

object TestEntity {
}
