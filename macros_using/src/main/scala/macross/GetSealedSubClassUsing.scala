package macross

/**
 * Created by yu jie shui on 2015/10/14 16:30.
 */
sealed trait A

case object B extends A

case object C extends A

object GetSealedSubClassUsing extends App {
  val a = GetSealedSubClass.ol[A]
  println(a)
}

sealed trait Hello[A]

case class Ohayo[A, B](a: (A, B)) extends Hello[A]

object GetSealed extends App {
  val a = MakeHasTypeParamFunc.ol3[Hello[_]]
  val b = a.asInstanceOf[ {def smartConstructors[A, B](a: (A, B)): Ohayo[A, B]}].smartConstructors(1, 2).a
  println(b)
}