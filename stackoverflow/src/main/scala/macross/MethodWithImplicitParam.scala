package macross

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}

/**
  * Created by yu jie shui on 2015/12/4 9:05.
  */
class MethodWithImplicitParam(name: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MethodWithImplicitParamImpl.apply

}

class MethodWithImplicitParamImpl(val c: Context) extends macross.base.ShowInfo {

  import c.universe._



  def apply(annottees: c.Expr[Any]*) = {
    val a = q"def a(i:Int):Int" match {
      case q"def $funcName (...$paramsList):$resultType" ⇒ paramsList
    }
    showInfo(show(a))
    q"{..${annottees}}"
  }
}