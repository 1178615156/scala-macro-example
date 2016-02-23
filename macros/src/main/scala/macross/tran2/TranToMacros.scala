package macross.tran2

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2016/1/26.
  */
trait TranRule

object TranToMacros {
  type Rule = (TranRule)

  def apply[In, To](implicit tranRule: TranToMacros.Rule): Any = macro TranMacrosImpl.apply[In, To]

}

class TranMacrosImpl(val c: scala.reflect.macros.whitebox.Context) extends macross.base.ShowInfo {
  self =>

  import c.universe._

  def apply[In: c.WeakTypeTag, To: c.WeakTypeTag](tranRule: c.Expr[TranToMacros.Rule]) = {
    val inType = c.weakTypeOf[In]
    val toType = c.weakTypeOf[To]
    //    showInfo(
    //      show(
    //        c.inferImplicitValue(c.typeOf[TranRule])
    //      )
    //    )
    showInfo(show(
      c.enclosingImplicits
    ))
    //    showInfo(show(
    //      c.eval[TranToMacros.Rule]
    //        (c.Expr[TranToMacros.Rule](c.untypecheck(tranRule.tree.duplicate)))
    //    ))
    q""
  }
}