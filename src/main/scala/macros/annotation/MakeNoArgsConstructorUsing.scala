package macros.annotation

/**
 * Created by YuJieShui on 2015/9/11.
 */
object MakeNoArgsConstructorUsing extends App {

  @MakeNoArgsConstructorMacros
  case class Module(i: Int, s: String)

  val m = new Module()
  println(m.i)//0
  println(m.s)//null
}
