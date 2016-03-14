//package macross.tran
//
//import scala.concurrent.{ExecutionContext, Future}
//import scala.language.experimental.macros
//import scala.reflect.macros.blackbox
//
//import macross.base.{TypeArgsList, ShowInfo}
//
///**
//  * Created by yujieshui on 2016/3/11.
//  */
//
//
//
//
//object Tran2 {
//  def apply[In, To](in: In)(implicit tranConfig: TranConfig): Any = macro Tran2Impl.applyTranTo[In, To]
//}
//
//class Tran2Impl(val c: blackbox.Context)
//  extends ShowInfo {
//  self =>
//
//  import c.universe._
//
//  case class TranMethodVal(name: TermName, form: Type, to: Type)
//
//  def applyTranTo[In: c.WeakTypeTag, To: c.WeakTypeTag](in: c.Expr[In])(tranConfig: c.Expr[TranConfig]): c.Tree = {
//    val inExpr = in
//
//    val tranExec = new TypeArgsList
//      with TranAlgorithm
//      with TranMacroInstance
//      with TranExe {
//      override val replaceRule: List[Replace] =
//        tranConfig.tree.symbol.typeSignature.members
//          .map(e => e.name.toTermName -> e.info.typeConstructor)
//          .filter { case (name, info) => info.contains(symbolOf[TranMethod[_, _]]) }
//          .map { case (name, info) =>
//            val tm = info.baseType(symbolOf[TranMethod[_, _]]).typeArgs
//            Replace(
//              (this getTypeList tm.head).reverse.tail.reverse,
//              (this getTypeList tm.tail.head).reverse.tail.reverse,
//              e => q""" $tranConfig.$name.apply($e) """
//            )
//          }.toList
//
//      override def to: TypeList = typeParamsList[To]
//
//      override def in: TypeList = typeParamsList[In]
//
//      override val c: self.c.type = self.c
//
//    }
//
//    val rt = {
//      import tranExec._
//      exe(ReplaceExpr(inExpr.tree, tranExec.in)).find(_.typeList >:> to)
//    }
//
//    showInfo(
//      s"""${show(tranExec.in)}  ->  ${show(tranExec.to)}
//         |${show(inExpr.tree)} -> ${show(rt.get.exprTree)}
//       """.stripMargin)
//
//    q"""
//   ${rt.get.exprTree}
//
//"""
//  }
//}
