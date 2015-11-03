package macross.tran

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
  def listTriangle(list: List[Type]): List[TypeList] = {
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