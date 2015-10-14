package macross

/**
 * Created by yu jie shui on 2015/10/14 16:30.
 */

object ObjUsing extends App {
  val a = Obj.ol[A]
  val b = Obj.olv[A](a)
  println(a)
}
