package so

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2015/12/1.
  */
class SuperClass extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro SuperClassImpl.apply

}

class SuperClassImpl(val c: Context) {

  def showInfo(s: String) =
    c.info(c.enclosingPosition, s.split("\n").mkString("\n |---macro info---\n |", "\n |", ""), true)

  import c.universe._

  def apply(annottees: c.Expr[Any]*) = {

    val superClass = annottees.map(_.tree).head match {
      case q"$mod class $name(..$params) extends ..$superClass { ..$body }" =>
        superClass: List[Tree]
    }
    showInfo(show(
      c.typecheck(annottees.map(_.tree).head).symbol.asClass.baseClasses.tail
    ))
    showInfo(showRaw(superClass))
    q"""
       {
       ..$annottees
       }
      """
  }
}
