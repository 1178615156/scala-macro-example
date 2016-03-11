package macross

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yujieshui on 2016/3/11.
  */
object NameValue {
  def apply[T](t: T): (String, T) = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context)(t: c.Expr[T]) = {
    import c.universe._
    val s: String = t.tree.toString()
    q"""{($s,$t)}"""
  }
}
