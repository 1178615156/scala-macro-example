package so

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/10/11.
 */

abstract class Api[T] {
  def a: Int

  def b: Int

  def t: List[T]
}

object TemplateParamsMacros {
  def apply[T](initValue: T): Api[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context)(initValue: c.Expr[T]): c.Tree = {
    import c.universe._
    val t: c.Type = c.weakTypeOf[T]//.typeSymbol.name.toTypeName
    q"""
      val api = new Api[$t] {
       def a = 1
       def b = 2
       override def t= List(${initValue})
     }
     api
    """
  }
}
