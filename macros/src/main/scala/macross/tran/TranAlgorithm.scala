package macross.tran

import scala.concurrent.Future
import scala.reflect.macros.blackbox

/**
 * Created by YuJieShui on 2015/11/3.
 */
trait TranAlgorithm {
  type Type
  type TypeList = List[Type]
  type ExprTree

  case class Replace(from: List[Type], to: List[Type], func: ExprTree => ExprTree)

  val replaceRule: List[Replace]

  /** list triangle
    * @example :see[[macross.tran.TranAlgorithmTest]]
    * @param list
    * @return
    */
  def listTriangle(list: List[Type]): List[TypeList] = {
    list.foldLeft(List[TypeList]()) { (l, r) ⇒
      val treeList =
        if (l.isEmpty) List(r) else l.last ::: List(r)

      l :+ treeList
    }
  }

  /**
   * @example :see[[macross.tran.TranAlgorithmTest]]
   * @param l
   * @return
   */
  def splitList(l: TypeList): List[(TypeList, TypeList)] = {
    val headTypeList: List[TypeList] = listTriangle(l) dropRight 1

    val tailTypeList: List[TypeList] = listTriangle(l.reverse).dropRight(1).reverse.map(_.reverse)

    headTypeList zip tailTypeList
  }

  case class ReplaceResult(replace: Replace, from: TypeList, to: TypeList, head: TypeList = Nil)

  /**
   * @example :see[[macross.tran.TranAlgorithmTest]]
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

  case class ReplaceExpr(exprTree: ExprTree, typeList: TypeList)

}

trait TranMacroInstance extends TranAlgorithm {
  self: TranAlgorithm ⇒
  val c: blackbox.Context
  type Type = c.universe.Type
  type ExprTree = c.universe.Tree
}

trait TranRule {
  self: TranAlgorithm with TranMacroInstance ⇒

  import c.universe._

  lazy val option: Type = c.typeOf[Option[_]].typeConstructor
  lazy val future: Type = c.typeOf[Future[_]].typeConstructor
  lazy val list: Type = c.typeOf[List[_]].typeConstructor


  lazy val replaceRule: List[Replace] = List(
    Replace(List(option, option), List(option), (i: ExprTree) => q"$i.flatten"),
    Replace(List(future, future), List(future), (i: ExprTree) => q"$i.flatMap(e=>e)"),
    Replace(List(option, future), List(future, option), (i: ExprTree) => q"$i.traverse"),
    Replace(List(list, future), List(future, list), (i: ExprTree) => q"$i.traverse")

  )
}

trait TranExe extends macross.base.ShowInfo{
  self: TranAlgorithm with TranMacroInstance with TranRule ⇒

  import c.universe._

  def in: TypeList

  def to: TypeList

  /**
   *
   * @param replaceExpr
   * @return
   */
  def exe(replaceExpr: ReplaceExpr): List[ReplaceExpr] = {
//    showInfo(show(replaceExpr))
    val ll = exeAllReplace(replaceExpr.typeList)
    if (replaceExpr.typeList == to || ll.isEmpty)
      List(replaceExpr)
    else {
      val replaceExprList = ll.flatten.map(e => {
        type MapFunc = ExprTree ⇒ ExprTree

        val newExprValue = e.head.foldRight(e.replace.func: MapFunc)((r, l) ⇒ {
          (e: ExprTree) ⇒ q"$e.map(e=>${l(q"${"e": TermName}")})"
        })(replaceExpr.exprTree)

        ReplaceExpr(newExprValue, e.head ++ e.to)
      })
      replaceExprList.flatMap(exe)
    }
  }
}





















