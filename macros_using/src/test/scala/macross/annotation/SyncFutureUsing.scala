package macross.annotation

import scala.concurrent.{Await, Future}

/**
  * Created by yujieshui on 2016/2/23.
  */
package a {

  object AF {
    def apply[T](future: Future[T]) = Await.result(future, scala.concurrent.duration.Duration.Inf)

    def apply[T](t: T) = t
  }

}

trait A[Key,Value]{
  def f (key: Key):Future[Value]
}
trait B{

}

trait SyncApiUsing[T] {
  def f(iiii: Int): Future[Int]
}

//@SyncApi(a.AF)
trait AAA[Key, Value] {
  def get(key: Key): Future[Option[Value]]

}

object M {
  def main(args: Array[String]) {
    val a = new AAA[Int,Int] {
      override def get(key: Int): Future[Option[Int]] = Future.successful(Some(1))
    }
//    println(new SyncApiUsing().sync.f(11))
//    println(new SyncApiUsing().sync.f(11))
  }

}