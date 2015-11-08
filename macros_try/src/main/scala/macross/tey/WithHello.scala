//package macross.teach
//
//import scala.annotation.{StaticAnnotation, compileTimeOnly}
//import scala.language.experimental.macros
//import scala.reflect.macros.blackbox.Context
//import scala.reflect.macros.blackbox.Context
//
///**
// * Created by YuJieShui on 2015/10/19.
// */
//class WithHello extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro WithHelloImpl.apply
//
//}
//
//class WithHelloImpl(val c: Context) extends macross.annotation.ClassWithFunc{
//  import c.universe._
//  def apply(list_annottees: List[Tree])={
//    list_annottees.toList
//  }
//}