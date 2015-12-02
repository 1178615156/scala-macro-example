package so

import macross.base.ShowInfo

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.macros.whitebox.Context


/**
  * Created by yjs on 2015/11/17.
  */
object SO33756799 {

  object ProxyMacro {
    type Implementor = (String, Any) => Any

    def proxy[T](implementor: Implementor): T = macro impl[T]

    def impl[T: c.WeakTypeTag](c: Context)(implementor: c.Expr[Implementor]): c.Expr[T] = {
      import c.universe._
      val thisc = c
      val si = new ShowInfo {
        override val c: blackbox.Context = thisc
      }
      val tpe = weakTypeOf[T]
      si.showInfo(show(tpe.decls))
      val decls = tpe.decls.map { decl =>
        val termName = decl.name.toTermName
        val method = decl.asMethod
        val params: List[List[c.universe.ValDef]] = method.paramLists.map(_.map(s => internal.valDef(s)))
        val paramVars = method.paramLists.flatMap(_.map { s =>
          internal.captureVariable(s)
          internal.referenceCapturedVariable(s)
          s.name
        })

        val r =
          q""" def $termName (...$params) = {
            $implementor (${termName.toString}, List(..${paramVars}) ).asInstanceOf[${method.returnType}]
           }"""
        si.showInfo(showCode(r))
        r
      }
      //      si.showInfo(show(decls.head))

      c.Expr[T] {
        q"""
      new $tpe {
        ..$decls
      }
  """
      }
    }
  }

}
