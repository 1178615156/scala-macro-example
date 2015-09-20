package macros.annotation.base

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/20.
 */
trait ShowInfo {
  val c: Context

  import c.universe._

  def showInfo(a: Any) = {
    c.info(c.enclosingPosition, show(a).split("\n").mkString("\n |---macro info---\n |", "\n |", ""), true)
  }

  def showCodeInfo(a: Tree) = {
    c.info(c.enclosingPosition, showCode(a), true)
  }

  def showRawInfo(any: Any) =
    c.info(c.enclosingPosition, showRaw(any), true)
}
