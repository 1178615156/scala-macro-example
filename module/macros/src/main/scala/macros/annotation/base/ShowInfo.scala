package macros.annotation.base

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/20.
 */
trait ShowInfo {
  val c: Context

  import c.universe._


  def showInfo(s: String) =
    c.info(c.enclosingPosition, s.split("\n").mkString("\n |---macro info---\n |", "\n |", ""), true)
}
