package yjs.macrs.common

/**
  * Created by yujieshui on 2016/8/20.
  */
trait AnnotationUtils {
  val c: scala.reflect.macros.blackbox.Context

  import c.universe._

  final def getAnnotation(x: MemberDef) = x.mods.annotations

  final def getAnnotationValue(tree: Tree): Seq[String] = tree match {
    case q"new $name (..$values)" => values collect {
      case AssignOrNamedArg(Ident(TermName(_)), Literal(Constant(s: String))) => s
      case Literal(Constant(s: String))                                       => s
    }
  }

  final def getAnnotationName(tree: Tree): String = tree match {
    case q"new $name (..$values)" => name.toString().split("\\.").last
  }
}
