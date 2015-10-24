package macross.annotation

import java.io.{File, PrintWriter}

import macross.annotation.base.ClassWithFunc
import macross.base.GetInClass

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
  extends GetInClass
  with ClassWithFunc {

  import c.universe._

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    val inClass = getInClass(annottees.map(_.tree).toList).head
    val fn = c.typecheck(inClass).symbol.fullName
    c.Expr(classWithFunc(inClass, List(q"def fullName=$fn")))
  }
}