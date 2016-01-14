package macross.tran

import slick.dbio.DBIO

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import utils.TraverseSequence._

/**
 * Created by YuJieShui on 2015/11/4.
 */
object Data {


  object a {
    val value = Option(Option(1))
    val need_result: Option[Int] = value.flatten
    type To = Option[Int]
  }

  object b {
    val value: Option[Future[Option[Future[List[Int]]]]] = Option(Future(Option(Future(List(2)))))
    val need_result: Future[Option[List[Int]]] =
      value.sequence.map(_.flatten).map(_.sequence).flatMap(e ⇒ e)
    type To = Future[Option[List[Int]]]
  }

  object c {
    val value: Option[List[Future[Int]]] = Option(List(Future(3)))
    val need_result: Future[Option[List[Int]]] =
      value.map(_.sequence).sequence
    type To = Future[Option[List[Int]]]
  }

}

object TranUsing extends App {

  import TranToMacros._

//  def TranMacrosApply(): Unit = {
//    import Data._
//
//    assert(
//      TranMacros[Option[Int]](a.value) == a.need_result)
//
//    assert(Await.result(
//      TranMacros[Future[Option[List[Int]]]](b.value) zip b.need_result map (e ⇒ e._1 == e._2)
//      , Inf))
//
//    assert(Await.result(
//      TranMacros[Future[Option[List[Int]]]](c.value) zip c.need_result map (e ⇒ e._1 == e._2)
//      , Inf))
//  }

  def TranTo(): Unit = {
    import Data._
    assert(
      a.value.tranTo[a.To] == a.need_result
    )
    assert(Await.result(
      b.value.tranTo[b.To] zip b.need_result map (e ⇒ e._1 == e._2)
      , Inf)
    )
  }

  TranTo()
  /// with tranTo
Some(Some(1)).tranTo[Option[Int]]

  List(DBIO.from(Future(1))).tranTo[DBIO[List[Int]]]
}