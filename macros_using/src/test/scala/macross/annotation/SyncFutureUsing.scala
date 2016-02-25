package macross.annotation

import scala.concurrent.{Await, Future}

/**
  * Created by yujieshui on 2016/2/23.
  */
package a{
  object AF {
    def apply[T](future: Future[T]) = Await.result(future, scala.concurrent.duration.Duration.Inf)
  }
}


@SyncApi(a.AF)
class SyncApiUsing[T] {
  def f(iiii: Int) = Future.successful("hello")

  private def fff = Future.successful(1)
}
@SyncApi(a.AF)
trait AAA[Key,Value]{
  def f(iiii: Int) = Future.successful("hello")
  def get(key: Key): Future[Option[Value]]
}

object M {
  def main(args: Array[String]) {
    println(new SyncApiUsing().sync.f(11))
    println(new SyncApiUsing().sync.f(11))
  }

}