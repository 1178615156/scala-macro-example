package macross.conf

import com.typesafe.config.{Config, ConfigFactory}
import macross.conf.conf.path
import org.scalatest.FunSuite

import scala.concurrent.duration._

/**
  * Created by yujieshui on 2016/5/23.
  */
object global_conf {

  implicit val config: Config = ConfigFactory.load()

  @conf
  @ConfCheck("application.conf")
  object hello {
    val name = config.getString(path /* == "hello.name" */)
        val world = config.getLong(path/* == "hello.world" */).second.toMillis
    val ss   = conf.as[String]
    val ll   = conf.as[List[Int]]

  }

}


//@conf
//@ConfCheck("application.conf")
//trait local_conf {
//  val config = ConfigFactory.load()
//  val world  = config.getString(conf.path)
//  class c {
//    val c = config.getString(conf.path)
//  }
//}


class confTest extends FunSuite {
  test("hello.name") {
    assert(global_conf.hello.name == "helloName")
  }
  test("hello.ss") {
    assert(global_conf.hello.ss == "ss")
    println(global_conf.hello.ss)
  }
}
