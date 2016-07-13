package yjs.macrs.conf

import com.typesafe.config.ConfigFactory

/**
  * Created by yuJieShui on 2016/7/13.
  */
object global_conf {
  private implicit val config = ConfigFactory.load()

  @conf
  object hello {

    val x = conf.as[Int]

    def y = conf.as[Int]

    object world {
      val l = conf.as[List[Int]]
    }

  }

  object world

}


class confTest extends org.scalatest.FunSuite {


  test("global_conf") {
    assert(global_conf.hello.x === 1)
    assert(global_conf.hello.y === 2)
    assert(global_conf.hello.world.l === List(1, 2, 3))
  }


}













