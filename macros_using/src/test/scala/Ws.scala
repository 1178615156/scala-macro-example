/**
  * Created by yuJieShui on 2016/1/27.
  */
class Ws {
  class A
  implicit def i : (Int) ⇒ A = i ⇒ new A

  def f (a:A) = ???
  f(1)
}
