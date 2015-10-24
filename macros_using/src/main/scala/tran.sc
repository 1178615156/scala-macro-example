val a = List(1, 2, 3, 4)

def fl[T](l: List[T]) = l.foldLeft(List[List[T]]()) { (l, r) ⇒
  if (l.isEmpty)
    l :+ List(r)
  else
    l :+ (l.last ::: List(r))
}
val b = fl(a).reverse.tail.reverse
b zip b.reverse
Map.apply(11l→"")