package macross.tran

import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by YuJieShui on 2015/11/4.
 */
class TranUsing extends App {

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

}

object TranAlgorithmTest extends App {
  val option = "option"
  val future = "future"
  val value = "value"

  val tranAlgorithm = new TranAlgorithm {
    override type Type = String
    override type ExprTree = String
    override val replaceRule: List[Replace] = List(
      Replace(List(option, option), List(option), (i: ExprTree) => s"$i.flatten"),
      Replace(List(future, future), List(future), (i: ExprTree) => s"$i.flatMap(e=>e)"),
      Replace(List(option, future), List(future, option), (i: ExprTree) => s"$i.traverse")
    )
  }

  import tranAlgorithm._

  val in: TypeList = List(option, future, option, option, value)
  val to: TypeList = List(future, option, option, value)

  //
  def listTriangleTest(): Unit = {

    assert(listTriangle(List(1).map(_.toString)) ==
      List(
        List(1)
      ).map(_.map(_.toString))
    )

    assert(
      listTriangle(List(1, 2, 3, 4).map(_.toString)) ==
        List(
          List(1),
          List(1, 2),
          List(1, 2, 3),
          List(1, 2, 3, 4)
        ).map(_.map(_.toString))
    )
    println(listTriangle(List(1, 2, 3, 4).map(_.toString)))
  }

  listTriangleTest()

  //
  def splitListTest(): Unit = {
    assert(splitList(List(1, 2, 3).map(_.toString)) == List(
      (List("1"), List("2", "3")),
      (List("1", "2"), List("3"))
    ))
    println(splitList(List(1, 2, 3).map(_.toString)))
  }

  splitListTest()

  //
  def exeReplaceTest(): Unit = {
    val rt = exeSingleReplace(in)

    assert(rt.map(e ⇒ e.head ++ e.to) ==
      List(List(future, option, option, option, value))
    )
    println(rt)
  }

  exeReplaceTest()

  //
  def exeAllReplaceTest(): Unit = {
    val rt = exeAllReplace(in)
    rt.map(_.map(e ⇒ e.head ++ e.to)) == List(
      List(List(future, option, option, option, value)),
      List(List(option, future, option, value)),
      List(List(option, future, option, value))
    )
    println(rt)
  }

  exeAllReplaceTest()

  //
  def exe(replaceExpr: ReplaceExpr): List[ReplaceExpr] = {
    val ll = exeAllReplace(replaceExpr.typeList)
    if (replaceExpr.typeList == to || ll.isEmpty)
      List(replaceExpr)
    else {
      val replaceExprList = ll.flatten.map(e => {
        type MapFunc = ExprTree ⇒ ExprTree

        val newExprValue = e.head.foldRight(e.replace.func: MapFunc)((r, l) ⇒ {
          (e: ExprTree) ⇒ s"$e.map(e=>${l("e")})"
        })(replaceExpr.exprTree)

        ReplaceExpr(newExprValue, e.head ++ e.to)
      })
      replaceExprList.flatMap(exe)
    }
  }

  val initExpr = "hello"


  val rt = exe(ReplaceExpr(initExpr, in)).find(_.typeList == to)
  assert(rt.nonEmpty)
  println(rt)

}