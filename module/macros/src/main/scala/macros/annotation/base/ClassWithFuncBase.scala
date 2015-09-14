package macros.annotation.base

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/11.
 */
trait ClassWithFuncBase {
  val c: Context

  import c.universe._
  //with function
  def classWithFunc(caseClass: Tree, listDef: List[Tree]): c.universe.Tree = {
    caseClass match {
      case q"$mod class $name(..$params) extends ..$bases { ..$body }" ⇒
        q"""
            $mod class $name(..$params) extends ..$bases {
            ..$body
            ..$listDef
            }
            """
    }
  }
}
