package macross.annotation

import scala.annotation.tailrec
import scala.util.control.TailCalls.TailRec

/**
  * Created by yuJieShui on 2016/1/7.
  */
class TailCallTest extends org.scalatest.FunSuite {
  def sumZero = 0

  /*
 def sumRecursiveImpl(n: Int)(rt: Int): Int = {
   if (n == 0)
     rt + 0
   else
     sumRecursiveImpl(n - 1)(n + rt)
 }
 */
  @TailCall(sumZero)
  def sum(n: Int): Int = {
    if (n == 0)
      0
    else if (n == 1)
      1
    else
      math.abs(0) + (n + 0) + sum(n - 1)
  }


  def timesZero = 1

  /*
   def
   */
  @tailrec
  final def stratum(n: Int, rt: Int = 1): Int = if (n == 0) rt else stratum(n - 1, rt * n)

  @TailCall(timesZero)
  def stratum(n: Int): Int =
    if (n == 0) 1 else n * stratum(n - 1)


//  @TailCall(sumZero)
//  def fib(n: Int): Int =
//    if (n == 0)
//      0
//    else if (n == 1)
//      1
//    else
//      fib(n - 1) + fib(n - 2)

//  @TailCall(sumZero)
//  def fib2(n: Int): Int =
//    if (n == 0)
//      0
//    else if (n == 1)
//      1
//    else
//      fib2(n - 1) + fib2(n - 2) * fib2(n - 3)
//
//  def fib2(n: Int, total: Int, rt2: Int, rt3: Int) =
//    if (n == 0)
//      0
//    else if (n == 1)
//      1
//    else
//      ???

  //    fib2(n-1,/*total = fib2(n - 1) + fib2(n - 2) + fib2(n-3)*/)
  def fib(n: Int, total: Int, rt2: Int): Int = {
    if (n == 0)
      0 + total
    else if (n == 1)
      1 + total
    else
      fib(n - 1, total + rt2, total)
  }

  test(" annotation tail call") {
    assert(sum(1) == 1)
    assert(sum(2) == 3)
    assert(sum(3) == 6)
  }

//  test("fib") {
//    assert(fib(0) == 0)
//    assert(fib(1) == 1)
//    assert(fib(2) == 1)
//    assert(fib(3) == 2)
//    assert(fib(4) == 3)
//    assert(fib(5) == 5)
//    fib(100)
//
//  }


}

















