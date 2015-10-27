val a = List(1, 2, 3, 4, 5, 6)
def rep = Map(
  List(1, 2) → List(3),
  List(3, 4, 5) → List(3)
)
val to = List(3, 3, 6)

def fl[T](l: List[T]): List[List[T]] = {
  l.foldLeft(List[List[T]]()) { (l, r) ⇒
    if (l.isEmpty)
      l :+ List(r)
    else
      l :+ (l.last ::: List(r))
  }
}
val b,l=a
val cHead=fl(b).reverse.tail.reverse ::: fl(l).reverse.tail.reverse
val c = fl(b.reverse).reverse.tail.map(_.reverse) ::: fl(l.reverse).reverse.tail.map(_.reverse)
fl(a).reverse.tail.reverse
fl(a.reverse).reverse.tail.map(_.reverse)
//
//def exeRep[T](l: List[T], rep: Map[List[T], List[T]]): List[T] = {
//  val b = fl(l).reverse.tail.reverse
//  val c = fl(l.reverse).reverse.tail.map(_.reverse)
//
//  val d = b zip c
//  (d filter (e ⇒ rep.get(e._1).nonEmpty)).flatMap(e ⇒ rep.get(e._1).get ::: e._2)
//}
//def r[T](l: List[T], result: List[T], rep: Map[List[T], List[T]]): Option[List[T]] = {
//  val b = exeRep(l, rep)
//  //  fl(l.reverse).reverse.tail.map(_.reverse).map(l⇒r(l,result,rep))
//  if (l == result)
//    Some(l)
//  else {
//    val b: List[T] = exeRep(l, rep)
//    if (b == l)
//      None
//    else {
//      val c = b :: fl(l.reverse).reverse.tail.map(_.reverse)
//      c.map(e ⇒ r(e, result, rep)).filter(_.nonEmpty).head
//    }
//  }
//
//}
//
//r(a, to, rep)