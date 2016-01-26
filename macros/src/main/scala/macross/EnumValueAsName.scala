//package macross
//
//import scala.annotation.StaticAnnotation
//import scala.language.experimental.macros
//import scala.reflect.macros.blackbox.Context
//
///**
//  * Created by yu jie shui on 2015/12/31 11:29.
//  */
//class EnumValueAsName extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro EnumValueAsNameImpl.impl
//}
//
///**
//  * @param c
//  */
//class EnumValueAsNameImpl(val c: Context) extends base.ShowInfo {
//
//  import c.universe._
//
//  def impl(annottees: c.Expr[Any]*) = {
//    val body: List[c.universe.Tree] = annottees.head.tree match {
//      case q"object $objectName extends (..$base) { ..$body }" ⇒
//        body: List[Tree]
//    }
//
//    showInfo(showRaw(body.head))
//    showInfo(showRaw(body(1)))
//
//    body collect {
//      case q"val $name = Value" ⇒
//      case q"val $name = Value(${v: Tree})" ⇒
//        showInfo(showRaw((v)))
//        showInfo(show(q"${c.prefix}.n"))
//    }
//    //s""
//    StringContext("You are ", " decades old, ", "!")
//
//    implicit class SizeStringContext(val sc: StringContext) {
//      def size(args: Any*): Int = sc.parts zip args map {
//        case (n, v) ⇒ n.size + v.toString.size
//      } sum
//    }
//    //.s (age / 10, name)
//    q"{..${annottees}}"
//
//  }
//}