package yjs.macrs.conf

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Created by yuJieShui on 2016/7/13.
  */
object global_conf {
  private implicit val config = ConfigFactory.parseString(
    """
      |hello {
      |  x = 1
      |  y = 2
      |  world{
      |    l = [1,2,3]
      |  }
      |}
      |world{
      | a = [
      |   {a=1},
      |   {b=1}
      | ]
      |}
      |abt = 123
    """.stripMargin)

  @conf
  object hello {

    val x = conf.as[Int]

    def y = conf.as[Int]

    object world {
      val l = conf.as[List[Int]]
    }

  }

  @conf
  object world {
    val a = conf.as[List[Config]]
  }
  @conf
  val abt = conf.as[Int]

}


class confTest extends org.scalatest.FunSuite {



  test("global_conf") {
    assert(global_conf.hello.x === 1)
    assert(global_conf.hello.y === 2)
    assert(global_conf.hello.world.l === List(1, 2, 3))
    val a :: b :: Nil = global_conf.world.a
    assert(a.getInt("a") === 1)
    assert(b.getInt("b") === 1)
    assert(global_conf.abt === 123)
    assert(global_conf.abt === 123)
  }


}













