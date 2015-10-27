import scala.concurrent.Future

import macross.TranMacros

import scala.language.higherKinds
import scala.reflect.ClassTag

/**
 * Created by YuJieShui on 2015/10/4.
 */
object Tran extends App {
  //  def tran[V, M[_], T[_ <: M[_]]](v: T[M[V]]) = {
  //  }
  //
  //
  //  implicit class WithTTo[In](val in: In) {
  //    def tto[To] = {
  //
  //    }
  //
  //  }
  //
  //
  //  Option(Option(1)).tto[Option]
  //    tran(Some(Some(1)))

  val a = TranMacros.typeList[Option[Future[List[Int]]]]
  println(a)

  TranMacros.apply[Option[Option[Int]], Option[Int]](Option(Option(1)))


}

//object Sum{
//
//  def sum(list: List[Int]):Int=
//  if (list.isEmpty)
//    0
//  else
//    list.head+sum(list.tail)
//}

object T extends App {
  val a = List(1, 2, 3, 4, 5, 6)

  def rep = Map(
    List(1, 2) → List(3),
    List(4, 5) → List(3)
  )

  def repList = Map(
    List(1, 2) → ((i: Int) ⇒ 1),
    List(4, 5) → ((i: Int) ⇒ 2)
  )

  val to = List(3, 3, 3, 6)

  def fl[T](l: List[T]): List[List[T]] = {
    l.foldLeft(List[List[T]]()) { (l, r) ⇒
      if (l.isEmpty)
        l :+ List(r)
      else
        l :+ (l.last ::: List(r))
    }
  }

  def exeRep[T](l: List[T], rep: Map[List[T], List[T]]): List[T] = {
    val b = fl(l).reverse.tail.reverse
    val c = fl(l.reverse).reverse.tail.map(_.reverse)

    val d = b zip c
    val rt: Option[List[T]] = (d filter (e ⇒ rep.get(e._1).nonEmpty)).map(e ⇒ rep.get(e._1).get ::: e._2).headOption
    if (rt.isEmpty)
      l
    else
      rt.get
  }

  def r[T](l: List[T], result: List[T], rep: Map[List[T], List[T]], head: List[T]): Option[List[T]] = {
    if (head ::: l == result)
      Some(head ::: l)
    else
    if (l.isEmpty || l.tail.isEmpty || l.tail.tail.isEmpty)
      None
    else {
      val b: List[T] = exeRep(l, rep)
      if (b == l) {
        val cHead: List[List[T]] = fl(b).reverse.tail.reverse ::: fl(l).reverse.tail.reverse
        val c = fl(b.reverse).reverse.tail.map(_.reverse) ::: fl(l.reverse).reverse.tail.map(_.reverse)
        (cHead zip c).map(e ⇒ r(e._2, result, rep, head ::: e._1)).find(_.nonEmpty).flatten
      }
      else {
        val b2: List[T] = exeRep(l, rep)
        val cHead = head :: fl(b).reverse.tail.reverse ::: fl(l).reverse.tail.reverse.map(e ⇒ head ::: e)
        val c = b :: fl(b.reverse).reverse.tail.map(_.reverse) ::: fl(l.reverse).reverse.tail.map(_.reverse)
        val d = (cHead zip c).map(e ⇒ {
          val ttt = r(e._2, result, rep, e._1)
          ttt
        })
        val e = d.find(_.nonEmpty).flatten
        e
      }
    }

  }

  val o = r(a, to, rep, Nil)
  println(o)
}