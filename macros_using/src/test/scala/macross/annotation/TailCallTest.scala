package macross.annotation

/**
  * Created by yuJieShui on 2016/1/7.
  */
class TailCallTest extends org.scalatest.FunSuite {
  @TailCall(0)
  def sum(n: Int): Int = {


    if (n == 0)
      0
    else if (n == 1)
      1
    else
      n + sum(n - 1)
  }



  def impl(n: Int, rt: Int): Int = {
    if (n == 0)
      rt
    else
      impl(n - 1, n + rt)
  }
  def zero = 0
  impl(10,zero)



  def sum2(n: Int)(rt: Int): Int = {

    if (n == 0)
      0 + rt
    else
      sum2(n - 1)(n + rt)
  }


  test(" annotation tail call") {
    assert(sum(1) == 1)
    assert(sum(2) == 3)
    assert(sum(3) == 6)
  }
  test(" annotation sum2 ") {
    println(sum2(1)(0))
    println(sum2(2)(0))
    println(sum2(3)(0))
    //    assert(sum2(1)(0) == 1)
    //    assert(sum2(2)(0) == 3)
    //    assert(sum2(3)(0) == 6)
  }
}

















