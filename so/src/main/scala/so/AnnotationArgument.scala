package so

import scala.io.Source
import scala.reflect.macros.blackbox
import java.io.{File, PrintWriter}

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros

/**
  * Created by yuJieShui on 2016/7/21.
  */

//use another class annotation save class info
class AnnotationArgumentClass[T](clazz: Class[T]) extends StaticAnnotation

class AnnotationArgument extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro AnnotationArgumentImpl.impl
}

class AnnotationArgumentImpl(val c: blackbox.Context) {

  import c.universe._

  def impl(annottees: c.Expr[Any]*): c.Tree = {
    val classValue = annottees.head.tree match {
      case x: MemberDefApi => x.mods.annotations collectFirst {
        case q"new $annotation($classValue)" => classValue
      }
    }

    import scala.reflect.internal.Types
    val x = (c.eval(c.Expr[Type](c.typecheck(classValue.get))))
    println(x.typeSymbol.fullName)

    q"..${annottees}"
  }
}