package macross.tey

/**
 * Created by YuJieShui on 2015/9/10.
 */
object GetClassOfUsing extends App {
  val a: Class[String] = GetClassMacros.apply[String]
  assert(a == classOf[String])
  println(a)
  //error
  //  def propaga[T]=GetClassMacros.getClass[T]
}
