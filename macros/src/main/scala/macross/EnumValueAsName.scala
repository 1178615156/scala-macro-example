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
//class EnumValueAsNameImpl(val c: Context) {
//  import c.universe._
//  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
//    annottees.head.tree match {
//      case q"object $objectName extends (..$base) { ..$body }"⇒
//
//    }
//  }
//}