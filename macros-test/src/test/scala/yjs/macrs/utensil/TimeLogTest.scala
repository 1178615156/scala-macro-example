package yjs.macrs.utensil

import org.scalatest.WordSpec
import org.slf4j.LoggerFactory


class TimeLogTest extends WordSpec{
  implicit val logger = LoggerFactory.getLogger(this.getClass)

  @TimeLog
  def hello(a:Int) = {
    println(1 + 1)
  }

  "hello" in{

    hello(1)
  }
}
