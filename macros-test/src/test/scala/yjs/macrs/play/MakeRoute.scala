package yjs.macrs.play

/**
  * Created by yuJieShui on 2016/7/15.
  */
@MakeRoute
@Routes.Path(path = "bbb")
class Hello{
  @Routes.Get(url = "a")
  def a = 1
}

class MakeRouteTest extends org.scalatest.FunSuite {

}























































