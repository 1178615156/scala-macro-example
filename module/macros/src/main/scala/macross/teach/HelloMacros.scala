package macross.teach

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/10.
 */
object HelloMacros {
  /**
   * must write return type in either hello or helloImpl
   */
  def apply(s:String):String=macro impl

  def impl(c:Context)(s:c.Expr[String]):c.Expr[String]={
    import c.universe._
    c.Expr(q""""hello:"+$s""")
  }
}
