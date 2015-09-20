import scala.beans.BeanProperty
import scala.language.implicitConversions

/**
 * Created by YuJieShui on 2015/9/14.
 */
case class BinTree[T](value: T, left: Option[BinTree[T]] = None, right: Option[BinTree[T]] = None)

object BinTree {
  implicit def binTree2OptionBinTree[T](binTree: BinTree[T]): Some[BinTree[T]] = Some(binTree)
}

object T extends App {
  val bt = BinTree(3,
    left = BinTree(5,
      right = BinTree(1,
        left = BinTree(4,
          left = BinTree(9)))),
    right = BinTree(2,
      left = BinTree(6,
        right = BinTree(7,
          right = BinTree(8))))
  )
  case class DistValue[Value](dist: Int, value: Value)

  def computeDist[T](binTree: Option[BinTree[T]], dist: Int): Option[BinTree[DistValue[T]]] = {
    if (binTree.isEmpty)
      None
    else
      Some(BinTree(DistValue(dist, binTree.get.value),
        left = computeDist(binTree.get.left, dist + 1),
        right = computeDist(binTree.get.right, dist - 1)))
  }

  val dd = BinTree(DistValue(0, bt.value),
    left = computeDist(bt.left, 1),
    right = computeDist(bt.right, -1))
@BeanProperty
  def show[T](binTree: List[Option[BinTree[DistValue[T]]]], rt: List[DistValue[T]]): List[DistValue[T]] = {
    if (rt.isEmpty)
      if (binTree.isEmpty)
        Nil
      else
        show(binTree.filter(_.nonEmpty).flatMap(e => List(e.get.left, e.get.right)),
          List(binTree.head.get.value))
    else
    if (binTree.filter(_.nonEmpty).isEmpty)
      rt
    else {

      val a = binTree.filter(_.nonEmpty).sortWith((l, r) => {
        l.get.value.dist > r.get.value.dist
      })
      val max = a.head.get.value
      val min = a.last.get.value
      val t1 = if (max.dist > rt.head.dist)
        max :: rt
      else
        rt
      val t2 = if (min.dist < t1.last.dist)
        t1 :+ min
      else
        t1
      show(binTree.filter(_.nonEmpty).flatMap(e => List(e.get.left, e.get.right)),
        t2)
    }

  }
  val a = show(List(Some(dd)), Nil).map(_.value)
  println(a)

}