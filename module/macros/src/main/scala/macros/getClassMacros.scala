package macros

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}



/**
 * Created by YuJieShui on 2015/9/10.
 */



object GetClassMacros {
  def apply[T]: Class[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context) = {
    import c.universe._
    c.Expr( q"""
      classOf[${c.weakTypeOf[T]}]
    """)
  }
}
