import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

val a = Future(1)
val b = Future(2)
val c = Future(3)
val d = Future(4)
val e = Future(5)


implicit class WithZZ[T](val f1: Future[T]) {
  def zz[T2](f: Future[T2]) = f1 zip f
}

implicit class WithZZ2[T1, T2](val f2: Future[(T1, T2)]) {
  def t[T1, T2, T](t: ((T1, T2), T)) = (t._1._1, t._1._2, t._2)

  def zz[T](f: Future[T]) = f2 zip f map t
}

implicit class WithZZ3[T1, T2, T3](val f2: Future[(T1, T2, T3)]) {
  def t[T1, T2, T3, T](t: ((T1, T2, T3), T)) = (t._1._1, t._1._2, t._1._3, t._2)

  def zz[T](f: Future[T]) = f2 zip f map t
}
Future
val z: Future[(Int, Int, Int, Int)] = a zz b zz c zz d


