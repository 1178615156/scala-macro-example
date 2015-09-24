package macros.annotation

import macross.annotation.MakeGetSetMacro

/**
 * Created by YuJieShui on 2015/9/11.
 */
object MakeGetSetUsing extends App {

  @MakeGetSetMacro
  case class Module(
                     i: Int = 2,
                     s: String,
                     o: Option[String],
                     n: Option[AnyRef] = None
                     )

  val a = new Module(s = "sss", o = Some("option"))
  println(a.getI)//2
  println(a.getO)//option
  println(a.getN)//null

}
