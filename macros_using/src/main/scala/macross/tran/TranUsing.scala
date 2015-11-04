package macross.tran

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by YuJieShui on 2015/11/4.
 */
object TranUsing extends App {

  import utils.Traverse._
  import TranMacros._

  val a_value = Option(Option(1))
  val a_need_result: Option[Int] =
    a_value.flatten
  assert(TranMacros[Option[Int]](a_value) == a_need_result)

  println(TranMacros[Option[Int]].apply(a_value))
  //

  val b_value: Option[Future[Option[Future[List[Int]]]]] =
    Option(Future(Option(Future(List(2)))))
  val b_need_result: Future[Option[List[Int]]] =
    b_value.traverse.map(_.flatten).map(_.traverse).flatMap(e ⇒ e)

  assert(
    Await.result(b_need_result, Inf) ==
      Await.result(TranMacros[Future[Option[List[Int]]]](b_value), Inf)
  )
  println(Await.result(TranMacros[Future[Option[List[Int]]]].apply(b_value), Inf))

  val c_value: Option[List[Future[Int]]] = Option(List(Future(3)))
  val c_need_result: Future[Option[List[Int]]] =
    c_value.map(_.traverse).traverse

  assert(
    Await.result(c_need_result, Inf) ==
      Await.result(TranMacros[Future[Option[List[Int]]]](c_value), Inf)
  )
}
