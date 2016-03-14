package macross.tran

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
/**
  * Created by yujieshui on 2016/3/14.
  */

trait TranMethod[In, To] {
}

trait TranConfig


object TranConfig extends TranConfig {

  trait Default extends TranConfig {
    final val option_option_2_option       = new TranMethod[Option[Option[_]], Option[_]] {
      def apply[T](in: Option[Option[T]]): Option[T] = in.flatten
    }
    final val list_future_2_future_list    = new TranMethod[List[Future[_]], Future[List[_]]] {
      def apply[T](in: List[Future[T]])(implicit executor: ExecutionContext): Future[List[T]] = Future.sequence(in)
    }
    final val future_future_2_future       = new TranMethod[Future[Future[_]], Future[_]] {
      def apply[T](in: Future[Future[T]]) = in.flatMap(e => e)
    }
    final val option_futur_2_future_option = new TranMethod[Option[Future[_]], Future[Option[_]]] {
      def apply[V](t: Option[Future[V]]): Future[Option[V]] = t match {
        case Some(x) => x.map(x => Some(x))
        case None => Future(None)
      }
    }
  }

  implicit final object Default extends Default

}
