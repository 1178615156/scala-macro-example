package so

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2015/11/30.
  */
class AnnotationPassVal(val name: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro AnnotationPassValImpl.apply
}

class AnnotationPassValImpl(val c: Context) {

  import c.universe._

  def showInfo(s: String) =
    c.info(c.enclosingPosition, s.split("\n").mkString("\n |---macro info---\n |", "\n |", ""), true)

  def apply(annottees: c.Expr[Any]*) = {
    val a = c.macroApplication

    //look macroApplication is what
    showInfo(show(a))


    val AnnotationName: Tree = a match {
      case q"new AnnotationPassVal(name = $name).macroTransform(..$a)" =>
        name: Tree
    }

    showInfo(show(AnnotationName))
    q"""{..$annottees}"""
  }
}