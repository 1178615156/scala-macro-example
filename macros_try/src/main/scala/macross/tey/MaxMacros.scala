package macross.tey

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/10.
 */
object MaxMacros {
  def apply(l: Int, r: Int): Int = macro impl

  def impl(c: Context)(l: c.Expr[Int], r: c.Expr[Int]) = {
    import c.universe._
    c.Expr( q"""
    val temp_l=$l
    val temp_r=$r
    if (temp_l>temp_r)
      temp_l
    else
      temp_r

    """)
  }
}
