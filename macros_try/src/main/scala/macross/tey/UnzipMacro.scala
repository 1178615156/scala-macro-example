//package macross.tey
//
//import scala.language.experimental.macros
//import scala.language.higherKinds
//import scala.reflect.macros.blackbox.Context
//
///**
//  * Created by yjs on 2015/11/16.
//  */
//object UnzipMacro {
//
//  trait Unzip[T, F] {
//    def unzip(v: F[T]): T
//  }
//
//  object Unzip {
//  }
//
//  implicit class WithUnzip[F[_], T](val v: F[T]) {
//    def unzz(implicit unzip: Unzip[T, F]): T = unzip.unzip(v)
//  }
//
//
//}
//
//class UnzipImpl(val c: Context) extends macross.base.ShowInfo {
//
//  import c.universe._
//
//  def apply[T, F[_ <: T ]]
//  (implicit
//   wf: c.WeakTypeTag[F[_ <: T ]],
//     wt: c.WeakTypeTag[T]
//  ): c.Expr[UnzipMacro.Unzip[T, F]] = {
//    val T_type = wt.tpe
//    val F_type = wf.tpe
//    c.Expr(
//      q"""
//  new Unzip[$T_type,$F_type]{
//    def unzip(v:$F_type[$T_type])= v.get
//  }
//  """
//    )
//  }
//}
