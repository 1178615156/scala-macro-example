package stackoverflow

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.whitebox

/**
 * Created by YuJieShui on 2015/10/22.
 */
class ImplicitVal {

}

object Foo {
  def foo: Unit = macro fooImpl

  def fooImpl(c: whitebox.Context): c.Expr[Unit] = {
    import c.universe._
    implicit val x: c.type = c
    val res = sysoutFQN

    c.Expr(q"$res")
  }

  def sysoutFQN(implicit c: whitebox.Context): c.universe.Tree = {
    import c.universe._

    q"java.lang.System.out.println()"
  }
}