//package macross.slick
//
//import macross.annotation.base.{AnnotationParam, ClassWithFunc}
//import macross.base.{IsBaseType, GetInClass, ShowInfo}
//
//import scala.concurrent.Future
//import scala.reflect.macros.blackbox.Context
//import scala.reflect.macros.blackbox.Context
//import scala.language.experimental.macros
//import scala.annotation.{compileTimeOnly, StaticAnnotation}
//
//
///**
// * Created by YuJieShui on 2015/9/26.
// */
//class SlickUnApply(val showInfo: Boolean) extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro SlickUnApplyImpl.apply
//}
//
//class SlickUnApplyImpl(val c: Context)
//  extends GetInClass
//  with ShowInfo
//  with ClassWithFunc
//  with IsBaseType
//  with AnnotationParam {
//
//  import c.universe._
//
//  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
//    val moduleDef=getInModule(annottees.map(_.tree ))
//    val classDef=getInClass(annottees.map(_.tree ))
//    q"""
//
//    def slickUnapply(a:${tq"${classDef.name}"})=Option(
//
//    )
//    """
//  }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
