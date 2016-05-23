package macross.conf

import com.typesafe.config.ConfigFactory
import macross.conf.conf.auto_conf_path
import org.scalatest.FunSuite

/**
  * Created by yujieshui on 2016/5/23.
  */
object global_conf {
  val config = ConfigFactory.load()
  @conf
  @conf_check("application.conf")
  object hello {
    val name = config.getString(auto_conf_path)
  }
}

//
//@conf
//@conf_check("application.conf")
//trait local_conf {
//  val config = ConfigFactory.load()
//  val world  = config.getString(auto_conf_path)
//  class c {
//    val c = config.getString(auto_conf_path)
//  }
//}


class confTest extends FunSuite{
  test("hello.name"){
    assert(global_conf.hello.name=="helloName")
  }
}
