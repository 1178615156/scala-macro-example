package macross.annotation

/**
  * Created by yujieshui on 2016/2/23.
  */
@SyncFuture
class SyncFutureUsing {
  def f(iiii: Int) = "hello"

  private def fff = 1
}


object M {
  def main(args: Array[String]) {
    println(new SyncFutureUsing().sync.f(11))
  }
}