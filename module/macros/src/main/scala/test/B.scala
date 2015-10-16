//package test
//
//import macross.base
//
//import scala.reflect.macros.{blackbox, whitebox}
//;
//
//object B {
//  def sub[A]: List[A] = macro SubImpl.apply[A]
//
//  class SubImpl(val c: blackbox.Context) extends base.ShowInfo {
//
//    import c.universe._
//
//    def apply[A: c.WeakTypeTag] = {
//      val ta = c.weakTypeOf[A]
//      val taSub: Set[c.universe.Symbol] = ta.typeSymbol.asClass.knownDirectSubclasses
//      showInfo(show(taSub.map(e => c.mirror.staticModule(e.fullName).typeSignature)))
//      val rt = q"List.apply[$ta](..${taSub.map(e => c.mirror.staticModule(e.fullName).fullName:TermName)})"
//      showInfo(show(rt))
//
//      c.Expr(rt)
//    }
//  }
//
//  def ensureUnique[A, B](subA: List[A])(op: A => B): Any = macro B.Impl.apply[A, B]
//
//  class Impl(val c: whitebox.Context) extends base.ShowInfo {
//
//    import c.universe._
//
//    def apply[A: c.WeakTypeTag, B: c.WeakTypeTag](subA: c.Expr[List[A]])(op: c.Expr[A => B]) = {
//      val ta = c.weakTypeOf[A]
//      val taSub = ta.typeSymbol.asClass.knownDirectSubclasses
//
//      if (taSub.isEmpty)
//        c.abort(c.enclosingPosition, ta.toString + "is not sealed")
//      //      showInfo(show(ta)+":"+show(taSub.map(_.fullName)))
//      val opVal = c.eval(c.Expr[(A) => B](c.untypecheck(op.tree)))
//      val objs =
//        c.eval(
//          subA
//          //          c.Expr[List[A]](
//          //            c.untypecheck(
//          //              q"List.apply[$ta](..${taSub.map(e => c.mirror.staticModule(e.fullName).typeSignature)})"
//          //            )
//          //          )
//        )
//      showInfo(show(opVal))
//      showInfo(show(objs))
//      //      showInfo(showRaw(q"List.apply()"))
//      q""
//    }
//  }
//
//}
