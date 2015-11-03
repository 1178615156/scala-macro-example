package utils

import scala.collection.generic.CanBuildFrom
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.{implicitConversions, higherKinds}
import scala.reflect.runtime.universe


/**
 * Created by yu jie shui on 2015/8/28 14:51.
 */

trait Traverse[V, T[_], M[_]] {
  def apply(t: T[M[V]]): M[T[V]]
}

object Traverse {
  implicit def traverseOptionFuture[V] = new Traverse[V, Option, Future] {
    override def apply(t: Option[Future[V]]): Future[Option[V]] = t match {
      case Some(x) => x.map(x => Some(x))
      case None => Future(None)
    }
  }

  implicit def traverseTraversableOnceFuture[A, M[X] <: TraversableOnce[X]]
  (implicit cbf: CanBuildFrom[M[Future[A]], A, M[A]], executor: ExecutionContext) = new Traverse[A, M, Future] {
    override def apply(t: M[Future[A]]): Future[M[A]] = Future.sequence[A, M](t)
  }


  def apply[V, T[_], M[_]](t: T[M[V]])(implicit traverse: Traverse[V, T, M]): M[T[V]] = {
    traverse.apply(t)
  }

  implicit class WithTraverse[V, T[_], M[_]](val t: T[M[V]]) {
    def traverse(implicit traverse: Traverse[V, T, M]): M[T[V]] = traverse.apply(t)
  }

}