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
  TranMacros.apply[Option[Option[Int]], Option[Option[Int]]](Option(Option(1)))
}
//object Sum{
//
//  def sum(list: List[Int]):Int=
//  if (list.isEmpty)
//    0
//  else
//    list.head+sum(list.tail)
//}