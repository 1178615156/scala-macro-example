package yjs.macrs.play

/**
  * Created by yuJieShui on 2016/7/15.
  */

@MakeRoute
@Routes.Path(path = "/aaa/")
@Routes.Path("/bbb/")
class Hello{
  def a = 1
  @Routes.Get(url = "url/b")
  def b =2

  @Routes.Post(url ="url/ccc")
  @Routes.Post(url ="url/cccxxx")
  def c (a:Int,b:String) = "3"
}


class MakeRouteTest extends org.scalatest.FunSuite {

}










