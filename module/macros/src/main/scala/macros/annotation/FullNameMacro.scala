package macros.annotation

import java.io.{File, PrintWriter}

import macros.annotation.base.{ClassWithFuncBase, GetInClassBase}

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by yu jie shui on 2015/9/14 15:14.
 */
class FullNameMacro extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro FullNameMacroImpl.impl
}

class FullNameMacroImpl(val c: Context)
  extends GetInClassBase
  with ClassWithFuncBase {

  import c.universe._

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    val inClass = getInClass(annottees.map(_.tree).toList)
    val fn = c.typecheck(inClass).symbol.fullName
    c.Expr(classWithFunc(inClass, List(q"def fullName=$fn")))
  }
}