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
      |xxx= 1
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
    val a = conf.as[List[Config]].map(e => e)
  }

  @conf
  val abt = conf.as[Int]


}
@conf.Start
object confApplyTest {
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
      |xxxx = 1
    """.stripMargin)

  val xxxx = conf[Int]

  object hello {

    val x = conf[Int]

    def y = conf[Int]

    object world {
      val l = conf[List[Int]]
    }

  }

  object world {
    val a = conf[List[Config]].map(e => e)
  }

  val abt = conf[Int]

}


class confTest extends org.scalatest.FunSuite {

  test("conf.apply") {
    import confApplyTest._
    assert(hello.x === 1)
    assert(hello.y === 2)
    assert(hello.world.l === List(1, 2, 3))
    val a :: b :: Nil = world.a
    assert(a.getInt("a") === 1)
    assert(b.getInt("b") === 1)
    assert(abt === 123)
    assert(abt === 123)
  }


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














