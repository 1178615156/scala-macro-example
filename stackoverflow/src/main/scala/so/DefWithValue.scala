package so

import scala.reflect.macros.blackbox.Context

import scala.language.experimental.macros


/**
  * Created by yu jie shui on 2015/12/7 15:40.
  */

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2015/12/1.
  */
class DefWithValue extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro DefWithValueImpl.apply

}

class DefWithValueImpl(val c: Context) extends macross.base.ShowInfo {

  import c.universe._

  def apply(annottees: c.Expr[Any]*) = {

    val out = annottees.head.tree match {
      case q"def $name (...$param):$resultType = {..$body}" ⇒ {
        q"""
            def $name (...$param):$resultType = {
            val ddd=1
              ..$body
            }
            """
      }
    }
    showInfo(show(out))
    q"""{  ${out} }"""
  }
}
