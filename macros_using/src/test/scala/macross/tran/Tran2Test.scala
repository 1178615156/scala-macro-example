package macross.tran

/**
  * Created by yujieshui on 2016/3/11.
  */
class Tran2Test extends org.scalatest.FunSuite {
  test("a") {
    Tran2.apply[Option[Option[_]], Option[_]](Some(Some(1)))
  }

}
