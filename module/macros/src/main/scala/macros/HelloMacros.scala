package macros
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}

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
    c.Expr(q"""
    "hello:"+$s
    """)
  }
}
