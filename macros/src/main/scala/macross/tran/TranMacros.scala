package macross.tran

import scala.concurrent.Future
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/10/4.
 */
class TranMacros[To] {
  def apply[In](inExpr: In): To = macro TranMacrosImpl.apply[In, To]
}

object TranMacros {
  def apply[To]: TranMacros[To] = new TranMacros[To]

  @deprecated
  def apply[In, To](inExpr: In): To = macro TranMacrosImpl.apply[In, To]
}

class TranMacrosImpl(val c: Context)
  extends macross.base.ShowInfo {
  self =>

  import c.universe._

  def typeParamsList[T: c.WeakTypeTag]: List[Type] = {
    def getTypeList(it: Type): List[Type] =
      if (it.typeArgs.isEmpty)
        List(it)
      else
        it.typeConstructor +: it.typeArgs.flatMap(getTypeList)
    getTypeList(c.weakTypeOf[T])
  }

  def apply[In: c.WeakTypeTag, To: c.WeakTypeTag](inExpr: c.Expr[In]): Tree = {
    val inTypeList = typeParamsList[In]
    val toTypeList = typeParamsList[To]
    //    val replaceMaxSize = replaceMap.keys.maxBy(_.productArity)
    object TranAlgorithmExe {
      val option: Type = c.typeOf[Option[_]].typeConstructor
      val future: Type = c.typeOf[Future[_]].typeConstructor
      val list: Type = c.typeOf[List[_]].typeConstructor
      val a = new TranAlgorithm {
        override type Type = c.universe.Type
        override type ExprTree = c.universe.Tree
        override val replaceRule: List[Replace] = List(
          Replace(List(option, option), List(option), (i: ExprTree) => q"$i.flatten"),
          Replace(List(future, future), List(future), (i: ExprTree) => q"$i.flatMap(e=>e)"),
          Replace(List(option, future), List(future, option), (i: ExprTree) => q"$i.traverse"),
          Replace(List(list, future), List(future, list), (i: ExprTree) => q"$i.traverse")

        )
      }

      import a._

      val in: TypeList = inTypeList
      val to: TypeList = toTypeList

      case class ReplaceExpr(exprTree: ExprTree, typeList: TypeList)

      /**
       *
       * @param replaceExpr
       * @return
       */
      def exe(replaceExpr: ReplaceExpr): List[ReplaceExpr] = {
        val ll = exeAllReplace(replaceExpr.typeList)
        if (replaceExpr.typeList == to || ll.isEmpty)
          List(replaceExpr)
        else {
          val replaceExprList = ll.flatten.map(e => {
            type MapFunc = ExprTree ⇒ ExprTree

            val newExprValue = e.head.foldRight(e.replace.func: MapFunc)((r, l) ⇒ {
              (e: ExprTree) ⇒ q"$e.map(e=>${l(q"${"e": TermName}")})"
            })(replaceExpr.exprTree)

            ReplaceExpr(newExprValue, e.head ++ e.to)
          })
          replaceExprList.flatMap(exe)
        }
      }

      //      val initExpr = "hello"


      def rt = exe(ReplaceExpr(inExpr.tree, in)).find(_.typeList == to)

      //      println(rt)
    }
    val rt = TranAlgorithmExe.rt
    showInfo(
      s"""${show(inTypeList)}  ->  ${show(toTypeList)}
         |${show(inExpr.tree)} -> ${show(rt.get.exprTree)}
       """.stripMargin)
    rt.get.exprTree
  }
}