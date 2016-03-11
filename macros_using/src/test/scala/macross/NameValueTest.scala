package macross

/**
  * Created by yujieshui on 2016/3/11.
  */
class NameValueTest extends org.scalatest.FunSuite {
  test("name value ") {
    def f(hello: String, world: String) = {
      assert(NameValue(hello) == ("hello", hello))
      assert(NameValue(world) == ("world", world))
      val varvar = 11
      assert(NameValue(varvar) == ("varvar", varvar))
    }
    f("aaaaa", "bbbbb")

  }
}
