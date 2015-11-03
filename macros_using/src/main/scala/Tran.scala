import macross.TranMacros

import scala.concurrent.Future
import scala.language.higherKinds

/**
  * Created by YuJieShui on 2015/10/4.
  */
object Treeran extends App {

  val a = TranMacros.typeList[Option[Future[List[Int]]]]
  println(a)

  TranMacros.apply[Option[Option[Int]], Option[Int]](Option(Option(1)))


}

object TranAlog2 {
  type Tree = String
  type TypeList = List[Tree]
  type ExprTree = String

  val option = "option"
  val future = "future"
  val value = "value"

  case class Replace(from: List[Tree], to: List[Tree], func: ExprTree => ExprTree)

  val replaceRule = List(
    Replace(List(option, option), List(option), (i: ExprTree) => s"$i.flatten"),
    Replace(List(future, future), List(future), (i: ExprTree) => s"$i.flatMap(e=>e)"),
    Replace(List(option, future), List(future, option), (i: ExprTree) => s"$i.traverse")
  )
  assert(replaceRule.map(e => e.from -> e).toMap.size == replaceRule.size)

  val initExpr = "hello"
  val in: TypeList = List(option, future, option, option, value)
  val to: TypeList = List(future, option, value)

  /** list triangle
    * @example :in:List(1, 2, 3, 4)
    *          out:List(
    *          List(1),
    *          List(1, 2),
    *          List(1, 2, 3),
    *          List(1, 2, 3, 4)
    *          )
    * @param list
    * @return
    */
  def listTriangle(list: List[Tree]): List[TypeList] = {
    list.foldLeft(List[TypeList]()) { (l, r) ⇒
      val treeList =
        if (l.isEmpty) List(r) else l.last ::: List(r)

      l :+ treeList
    }
  }

  def splitList(l: TypeList): List[(TypeList, TypeList)] = {
    val headTypeList: List[TypeList] = listTriangle(l) dropRight 1

    val tailTypeList: List[TypeList] = listTriangle(l.reverse).dropRight(1).reverse.map(_.reverse)

    headTypeList zip tailTypeList
  }

  case class ReplaceResult(replace: Replace, from: TypeList, to: TypeList, head: TypeList = Nil)

  /**
    * @example   val replaceRule = List(
    *            Replace(List(1, 2), List(3), (i: Tree) => 1),
    *            Replace(List(1, 2 ,3 ), List(3), (i: Int) => 2)
    *            )
    *            in: List(1,2,3,4)
    *            out: List((..,3,3,4),(..,3,4))
    * @param l
    * @return
    */
  def exeSingleReplace(l: TypeList): List[ReplaceResult] = {
    splitList(l) filter {
      case (head, tail) => replaceRule.exists(_.from == head)
    } map {
      case (head, tail) => replaceRule.find(_.from == head) map (replace => ReplaceResult(replace, l, replace.to ++ tail))
    } filter (_.nonEmpty) map (_.get)
  }

  def exeAllReplace(l: TypeList): List[List[ReplaceResult]] = {
    (Nil -> l) +: splitList(l) map {
      case (head, tail) => exeSingleReplace(tail).map(_.copy(head = head))
    } filter (_.nonEmpty)
  }

}

case class AB(i: Int, n: Int)

object TranAlgoTest extends App {
  TranAlgo.in

  import TranAlog2._

  //  println(listTriangle(in) dropRight 1)
  //  println(listTriangle(in.reverse).dropRight(1).map(_.reverse) reverse)

  def listTriangleTest(): Unit = {
    assert(listTriangle(List(1).map(_.toString)) ==
      List(
        List(1)
      )
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

  //  listTriangleTest()
  case class ReplaceExpr(exprTree: ExprTree, typeList: TypeList)


  def exe(replaceExpr: ReplaceExpr) = {
    val ll: List[List[ReplaceResult]] = exeAllReplace(replaceExpr.typeList)
    ll.flatten.map(e => {
      val typeList = e.head ++ e.to
      //      def headToMapExpr(list: TypeList, f: ExprTree=>ExprTree) = {
      //        if (list.isEmpty)
      //          (e: ExprTree) => s"${replaceExpr.exprTree}.map(${f(e)})"
      //        else
      //          headToMapExpr(list.tail,)
      //      }
      //      headToMapExpr(e.head)
      //      e.head.map(_ => (e: ExprTree) => s"$l.map($e)")
      //      e.head.foldLeft(replaceExpr.exprTree)((l, r) => {
      //      })
      val exprTree = replaceExpr.exprTree

      1
    })
  }

  val replaceExpr = ReplaceExpr(initExpr, in)
  val ll: List[List[ReplaceResult]] = exeAllReplace(replaceExpr.typeList)
  val head = ll.flatten.map { replaceResult =>
    replaceResult.head
  }
  println(head)
  val rt = exe(ReplaceExpr(initExpr, in))
  println(rt)

  def exeReplaceTest(): Unit = {
    val rt = exeSingleReplace(in)
    println(rt)
  }

  def exeAllReplaceTest(): Unit = {
    val rt: List[List[ReplaceResult]] = (exeAllReplace(in))
    println(rt)
  }

  //  exeReplaceTest()
  //  exeAllReplaceTest()
}

object TranAlgo {
  type Tree = Int
  type TreeList = List[Tree]
  type ExprTree = String

  val option = 1
  val future = 2
  val value = -1

  case class Replace(from: List[Tree], to: List[Tree], func: ExprTree => ExprTree)

  val replaceRule = List(
    Replace(List(option, option), List(option), (i: ExprTree) => s"$i.flatten"),
    Replace(List(future, future), List(future), (i: ExprTree) => s"$i.flatMap(e=>e)"),
    Replace(List(option, future), List(future, option), (i: ExprTree) => s"$i.traverse")
  )
  assert(replaceRule.map(e => e.from -> e).toMap.size == replaceRule.size)

  def rep = Map(
    List(1, 2) → List(3),
    List(4, 5) → List(3)
  )

  def repList = Map(
    List(1, 2) → ((i: Int) ⇒ 1),
    List(4, 5) → ((i: Int) ⇒ 2)
  )

  val in: TreeList = List(option, future, option, value)
  val to: TreeList = List(future, option, value)

  /** list triangle
    * @example :in:List(1, 2, 3, 4)
    *          out:List(
    *          List(1),
    *          List(1, 2),
    *          List(1, 2, 3),
    *          List(1, 2, 3, 4)
    *          )
    * @param list
    * @return
    */
  def listTriangle(list: List[Tree]): List[TreeList] = {
    list.foldLeft(List[TreeList]()) { (l, r) ⇒
      val treeList =
        if (l.isEmpty) List(r) else l.last ::: List(r)

      l :+ treeList
    }
  }

  def splitList(l: TreeList): List[(TreeList, TreeList)] = {
    val headTypeList: List[TreeList] = listTriangle(l) dropRight 1

    val tailTypeList: List[TreeList] = listTriangle(l.reverse).dropRight(1).reverse.map(_.reverse)

    headTypeList zip tailTypeList
  }

  case class ReplaceResult(replace: Replace, from: TreeList, to: TreeList, head: TreeList = Nil)

  /**
    * @example   val replaceRule = List(
    *            Replace(List(1, 2), List(3), (i: Tree) => 1),
    *            Replace(List(1, 2 ,3 ), List(3), (i: Int) => 2)
    *            )
    *            in: List(1,2,3,4)
    *            out: List((..,3,3,4),(..,3,4))
    * @param l
    * @return
    */
  def exeSingleReplace(l: TreeList): List[ReplaceResult] = {
    splitList(l) filter {
      case (head, tail) => replaceRule.exists(_.from == head)
    } map {
      case (head, tail) => replaceRule.find(_.from == head) map (replace => ReplaceResult(replace, l, replace.to ++ tail))
    } filter (_.nonEmpty) map (_.get)
  }

  def exeAllReplace(l: TreeList): List[List[ReplaceResult]] = {
    (Nil -> l) +: splitList(l) map {
      case (head, tail) => exeSingleReplace(tail).map(_.copy(head = head))
    } filter (_.nonEmpty)
  }


  def exeRep(l: List[Tree], rep: Map[List[Tree], List[Tree]]): List[Tree] = {

    val headTypeList = {
      val a = listTriangle(l)
      a.take(a.size - 1)
    }

    val tailTypeList = {
      val temp = listTriangle(l.reverse)
      (temp take (temp.size - 1)).reverse map (_.reverse)
    }

    val d = headTypeList zip tailTypeList
    headTypeList zip tailTypeList filter {
      case (head, tail) =>
        replaceRule.exists(_.from == head)
    }
    val rt: Option[List[Tree]] = (d filter (e ⇒ rep.get(e._1).nonEmpty)).map(e ⇒ rep.get(e._1).get ::: e._2).headOption
    if (rt.isEmpty)
      l
    else
      rt.get
  }

  def r(l: List[Tree], result: List[Tree], rep: Map[List[Tree], List[Tree]], head: List[Tree]): Option[List[Tree]] = {
    if (head ::: l == result)
      Some(head ::: l)
    else if (l.isEmpty || l.tail.isEmpty || l.tail.tail.isEmpty)
      None
    else {
      val b: List[Tree] = exeRep(l, rep)
      if (b == l) {
        val cHead: List[List[Tree]] = listTriangle(b).reverse.tail.reverse ::: listTriangle(l).reverse.tail.reverse
        val c = listTriangle(b.reverse).reverse.tail.map(_.reverse) ::: listTriangle(l.reverse).reverse.tail.map(_.reverse)
        (cHead zip c).map(e ⇒ r(e._2, result, rep, head ::: e._1)).find(_.nonEmpty).flatten
      }
      else {
        val b2: List[Tree] = exeRep(l, rep)
        val cHead = head :: listTriangle(b).reverse.tail.reverse ::: listTriangle(l).reverse.tail.reverse.map(e ⇒ head ::: e)
        val c = b :: listTriangle(b.reverse).reverse.tail.map(_.reverse) ::: listTriangle(l.reverse).reverse.tail.map(_.reverse)
        val d = (cHead zip c).map(e ⇒ {
          val ttt = r(e._2, result, rep, e._1)
          ttt
        })
        val e = d.find(_.nonEmpty).flatten
        e
      }
    }

  }

  //  val o = r(in, to, rep, Nil)
  //  println(o)
}

