package macross.tran

import scala.concurrent.Future
import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.blackbox.Context

/**
  * Created by YuJieShui on 2015/10/4.
  */

trait TranToMacros {

  trait TranTo[In, To] {
    def tranTo(in: In)(implicit tranConfig: TranConfig): To
  }

  object TranTo {
    implicit def implicitTranTo[In, To](implicit tranConfig: TranConfig): TranTo[In, To] = macro TranMacrosImpl.applyOfTranConfig[In, To]
  }

  implicit class WithTranTo[In](val in: In) {
    def tranTo[To](implicit tranTo: TranTo[In, To], tranConfig: TranConfig): To = tranTo.tranTo(in)
  }

}

object TranToMacros extends TranToMacros


class TranMacrosImpl(val c: Context) extends macross.base.ShowInfo with macross.base.TypeArgsList {
  self =>

  import c.universe._

  def implOfTranConfig[In: c.WeakTypeTag, To: c.WeakTypeTag](inExpr: c.Expr[In], tranConfig: c.Expr[TranConfig]): c.Tree = {

    val inTypeList = typeParamsList[In]
    val toTypeList = typeParamsList[To]
    val tranConfig_ = tranConfig

    object Tran extends TranAlgorithm with TranMacroInstance with macross.base.TypeArgsList with TranRuleOfTranConfig with TranExe {
      override val c : self.c.type = self.c
      lazy     val in: TypeList    = inTypeList
      lazy     val to: TypeList    = toTypeList

      override def tranConfig: c.Expr[TranConfig] = tranConfig_
    }
    val rt = {
      import Tran._
      exe(ReplaceExpr(inExpr.tree, in)).find(_.typeList >:> to)
    }

    showInfo(
      s"""${show(inTypeList)}  ->  ${show(toTypeList)}
         |${show(inExpr.tree)} -> ${show(rt.get.exprTree)}
       """.stripMargin)

    rt.get.exprTree
  }

  def applyOfTranConfig[In: c.WeakTypeTag, To: c.WeakTypeTag](tranConfig: c.Expr[TranConfig]) = {
    val inType = c.weakTypeOf[In]
    val toType = c.weakTypeOf[To]

    showInfo(show(inType))
    q"""
  new TranToMacros.TranTo[$inType,$toType]{
    def tranTo(in : $inType)(implicit tranConfig: TranConfig):$toType=${implOfTranConfig[In, To](c.Expr(q"in"), tranConfig)}
  }
  """
  }

  //
  //  @deprecated
  //  def apply[In: c.WeakTypeTag, To: c.WeakTypeTag](inExpr: c.Expr[In]): Tree = {
  //    val inTypeList = typeParamsList[In]
  //    val toTypeList = typeParamsList[To]
  //
  //    object Tran extends TranAlgorithm with TranMacroInstance with TranRule with TranExe {
  //      override val c : self.c.type = self.c
  //      lazy     val in: TypeList    = inTypeList
  //      lazy     val to: TypeList    = toTypeList
  //    }
  //
  //
  //    val rt = {
  //      import Tran._
  //      exe(ReplaceExpr(inExpr.tree, in)).find(_.typeList >:> to)
  //    }
  //
  //    showInfo(
  //      s"""${show(inTypeList)}  ->  ${show(toTypeList)}
  //         |${show(inExpr.tree)} -> ${show(rt.get.exprTree)}
  //       """.stripMargin)
  //
  //
  //    rt.get.exprTree
  //  }
  //
  //  def applyTranTo[In: c.WeakTypeTag, To: c.WeakTypeTag] = {
  //    val inType = c.weakTypeOf[In]
  //    val toType = c.weakTypeOf[To]
  //
  //    q"""
  //  new TranToMacros.TranTo[$inType,$toType]{
  //    def tranTo(in : $inType):$toType=${apply[In, To](c.Expr(q"in"))}
  //  }
  //  """
  //  }
}
