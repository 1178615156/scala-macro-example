package utils

import scala.collection.generic.CanBuildFrom
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.{implicitConversions, higherKinds}
import scala.reflect.runtime.universe


/**
  * Created by yu jie shui on 2015/8/28 14:51.
  */

import scala.collection.generic.CanBuildFrom
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.{implicitConversions, higherKinds}
import scala.reflect.runtime.universe


/**
  * Created by yu jie shui on 2015/8/28 14:51.
  */
trait TraverseSequence[V, T[_], M[_]] {
  def apply(t: T[M[V]]): M[T[V]]
}


trait TraverseSequenceImplicitValue {
  implicit def traverseOptionFuture[V] = new TraverseSequence[V, Option, Future] {
    override def apply(t: Option[Future[V]]): Future[Option[V]] = t match {
      case Some(x) => x.map(x => Some(x))
      case None => Future(None)
    }
  }

  implicit def traverseTraversableOnceFuture[A, M[X] <: TraversableOnce[X]]
  (implicit cbf: CanBuildFrom[M[Future[A]], A, M[A]], executor: ExecutionContext) = new TraverseSequence[A, M, Future] {
    override def apply(t: M[Future[A]]): Future[M[A]] = Future.sequence[A, M](t)
  }
}

object TraverseSequence extends TraverseSequenceImplicitValue {

  implicit class WithSequence[V, T[_], M[_]](val t: T[M[V]]) {
    def sequence(implicit traverse: TraverseSequence[V, T, M]): M[T[V]] = traverse.apply(t)
  }

}
