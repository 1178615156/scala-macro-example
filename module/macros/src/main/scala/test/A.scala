//package macross
//
//import macross.base.ShowInfo
//
//import scala.annotation.StaticAnnotation
//import scala.language.experimental.macros
//import scala.language.postfixOps
//import scala.reflect.macros.blackbox
//import scala.reflect.macros.blackbox.Context
//
///**
// * Created by YuJieShui on 2015/10/13.
// */
//sealed trait BotUpdate
//
//case object BBB extends BotUpdate
//
//trait CCC extends BotUpdate
//
//final class BotInterface[T] extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro BotInterfaceImpl.apply
//}
//
//class BotInterfaceImpl(val c: blackbox.Context)
//  extends ShowInfo {
//  import c.universe._
//  def apply(annottees: c.Expr[Any]*) = {
//    c.macroApplication match {
//      case q"new $name (..$param).$fn(..$bn)" =>
//        val n: AppliedTypeTree = name.asInstanceOf[AppliedTypeTree]
//        showInfo(showRaw(n.symbol))
//    }
//    annottees.head.tree
//  }
//}
//
////
////object BotInterfaceImpl {
////  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
////    import c.universe._
////    val cc = c
////    val updates = c.mirror.staticClass("macross.BotUpdate").asClass.knownDirectSubclasses
////    //    collect {
////    //      case cs ⇒ cs
////    //    }
////    new ShowInfo {
////      override val c: blackbox.Context = cc
////    }.showInfo(show(c.mirror.staticClass("macross.BotUpdate").asClass.knownDirectSubclasses))
////
////    val (updCallbacks, updDefs) = updates map { decl ⇒
////      val methodName = s"on${decl.name.decodedName.toString}"
////      val method = TermName(methodName)
////      ((decl, method), q"def $method(upd: $decl): Unit")
////    } unzip
////
////    val updCases = updCallbacks map {
////      case (upd, method) ⇒
////        cq"""
////           u: $upd => $method(u)
////          """
////    }
////
////    val onUpdateDef =
////      q"""def onUpdate(upd: macross.BotUpdate): Unit = {
////            upd match {
////              case ..$updCases
////            }
////          }"""
////
////    annottees map (_.tree) toList match {
////      case q"$mods trait $traitName extends ..$parents { ..$body }" :: Nil ⇒
////        c.Expr[Any](
////          q"""
////               $mods trait $traitName extends ..$parents {
////                 ..$updDefs
////                 $onUpdateDef
////                 ..$body
////               }
////             """
////        )
////      case q"class $className(..$args) extends ..$parents { ..$body }" :: Nil ⇒
////        c.Expr[Any](
////          q"""
////                class $className(..$args) extends ..$parents {
////                  ..$updDefs
////                  $onUpdateDef
////                  ..$body
////                }
////            """
////        )
////    }
////  }
////}