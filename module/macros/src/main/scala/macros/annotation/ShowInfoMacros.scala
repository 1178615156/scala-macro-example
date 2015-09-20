package macros.annotation

import macros.annotation.base.GetInClass

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}

/**
 * Created by YuJieShui on 2015/9/20.
 */
object ShowInfo {

  class ShowRaw extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro ShowInfoImpl.ShowRawImpl.apply
  }

  class Show extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro ShowInfoImpl.ShowImpl.apply
  }

  class showCode extends StaticAnnotation {
    def macroTransform(annottees: Any*): Any = macro ShowInfoImpl.ShowCodeImpl.apply
  }

}

object ShowInfoImpl {

  class ShowRawImpl(val c: Context) extends base.ShowInfo with GetInClass {

    import c.universe._

    def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
      val a: Seq[c.universe.Tree] = annottees.map(_.tree)
      val inclass = getInClass(a.toList)
      showInfo(showRaw(inclass))
      c.Expr[Any](Block(a.toList, Literal(Constant(()))))
    }
  }

  class ShowImpl(val c: Context) extends base.ShowInfo with GetInClass {

    import c.universe._

    def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
      val a: Seq[c.universe.Tree] = annottees.map(_.tree)
      val inclass = getInClass(a.toList)
      showInfo(show(inclass))
      c.Expr[Any](Block(a.toList, Literal(Constant(()))))
    }
  }

  class ShowCodeImpl(val c: Context) extends base.ShowInfo with GetInClass {

    import c.universe._

    def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
      val a: Seq[c.universe.Tree] = annottees.map(_.tree)
      val inclass = getInClass(a.toList)
      showInfo(showCode(inclass))
      c.Expr[Any](Block(a.toList, Literal(Constant(()))))
    }
  }

}





//
//class ShowInfoMacros extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro ShowInfoImpl.apply
//}
//
//class ShowInfoImpl(val c: Context) extends base.ShowInfo {
//
//  import c.universe._
//
//  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
//    val a: Seq[c.universe.Tree] = annottees.map(_.tree)
//    val inClass = a.collect {
//      case cc: ClassDef => cc
//    }.head
//
//
//
//    showInfo(inClass.mods)
//    showInfo(inClass.impl.parents)
//    showInfo(a.head.asInstanceOf[ClassDef].impl.body.collect {
//      case q"def $f_name(..$params):$f_type = $f_impl" => f_name -> f_type
//
//      case q"def $f_name:$f_type = $f_impl" => f_name -> f_type
//    })
//    c.Expr[Any](Block(a.toList, Literal(Constant(()))))
//  }
//}
