package macross.annotation

import macross.annotation.{MakeGetSetMacro, MakeNoArgsConstructorMacros}

/**
 * Created by YuJieShui on 2015/9/11.
 */
object MakeNoArgsConstructorUsing extends App {

  @MakeGetSetMacro
  @MakeNoArgsConstructorMacros
  case class Module(i: Int, s: String)

  val m = new Module()
  println(m.getI) //0
  println(m.getS) //null
}
