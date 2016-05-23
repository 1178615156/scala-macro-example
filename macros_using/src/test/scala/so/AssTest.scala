package so.aaa


/**
  * Created by yuJieShui on 2016/3/24.
  */
class AssTest extends org.scalatest.FunSuite {
  test("ass") {
    import so.AssertEquals.WithAssertEquals

    assert(1.assertEquals(2) == false)
    assert(2.assertEquals(2) == true)
    assert("a".assertEquals("a") == true)
    assert("a".assertEquals("b") == false)
    assert("a".assertEquals(1) == false)
  }
}
