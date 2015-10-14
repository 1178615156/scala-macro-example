package macross

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by yu jie shui on 2015/10/14 16:28.
 */

sealed trait A

case object B extends A

case object C extends A

object Obj {
  def ol[T]: List[T] = macro Obj.apply[T]

  def olv[T](l: List[T]): Any = macro Obj.olvImpl[T]
}

class Obj(val c: Context) extends base.ShowInfo {

  import c.universe._

  List.apply()

  def apply[T: c.WeakTypeTag] = {
    val sub = c.weakTypeOf[T].typeSymbol.asClass.knownDirectSubclasses
    showInfo(showRaw(sub))
    val rt = q"List(..${
      sub.map(e ⇒ {
        val fn = e.asClass.module.fullName: TermName

        q"${e.asClass.module}"
      })
    })"
    showInfo(showRaw(rt))

    //    c.eval(
    //      c.Expr[List[T]](rt)
    //              c.Expr[List[T]](c.typecheck(rt.duplicate)).value
    //    )
    rt
  }

  def olvImpl[T: c.WeakTypeTag](l: c.Expr[List[T]]) = {
    //    *       | val x1 = c.Expr[String](c.untypecheck(x.tree.duplicate))
    val a = l.tree match {
      case Literal(Constant(str: Any)) ⇒ str
    }
    //    c.eval(c.Expr[List[T]](c.typecheck(l.tree)))
    q""
  }

}
