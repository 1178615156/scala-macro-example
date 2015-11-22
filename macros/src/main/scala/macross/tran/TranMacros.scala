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
    def tranTo(in: In): To
  }

  object TranTo {
    implicit def implicitTranTo[In, To]: TranTo[In, To] = macro TranMacrosImpl.applyTranTo[In, To]
  }

  implicit class WithTranTo[In](val in: In) {
    def tranTo[To](implicit tranTo: TranTo[In, To]): To = tranTo.tranTo(in)
  }

}

class TranMacros[To] {
  def apply[In](inExpr: In): To = macro TranMacrosImpl.apply[In, To]
}

object TranMacros extends TranToMacros {
  def apply[To]: TranMacros[To] = new TranMacros[To]
}

class TranMacrosImpl(val c: Context) extends macross.base.ShowInfo {
  self =>

  import c.universe._

  def typeParamsList[T: c.WeakTypeTag]: List[Type] = {

    def getTypeList(it: Type): List[Type] =
      if (it.typeArgs.isEmpty)
        List(it)
      else
        it.typeConstructor +: it.typeArgs.flatMap(getTypeList)

    getTypeList(c.weakTypeOf[T].dealias)
  }

  def apply[In: c.WeakTypeTag, To: c.WeakTypeTag](inExpr: c.Expr[In]): Tree = {
    val inTypeList = typeParamsList[In]
    val toTypeList = typeParamsList[To]

    object Tran extends TranAlgorithm with TranMacroInstance with TranRule with TranExe {
      override val c: self.c.type = self.c
      lazy val in: TypeList = inTypeList
      lazy val to: TypeList = toTypeList
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

  def applyTranTo[In: c.WeakTypeTag, To: c.WeakTypeTag] = {
    val inType = c.weakTypeOf[In]
    val toType = c.weakTypeOf[To]

    q"""
  new TranMacros.TranTo[$inType,$toType]{
    def tranTo(in : $inType):$toType=${apply[In,To](c.Expr(q"in"))}
  }
  """
  }
}
