package macross

import macross.base.ShowInfo

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2015/12/6.
  */
trait ControlMacroLog

object ControlMacroLog {

  implicit
  object showInfo extends ControlMacroLog

  object notShowInfo extends ControlMacroLog

}

trait ControlMacroLogMacro {
  val c: blackbox.Context

  implicit class WithAsShowInfo(controlMacroLog: c.Expr[ControlMacroLog]) {
    def needShow = controlMacroLog.tree.symbol.name.toString == "showInfo"
  }

}

private[macross] object ControlMacroLogTest {
  def apply(implicit controlMacroLog: ControlMacroLog): Any = macro Impl.apply

  class Impl(val c: blackbox.Context) extends ShowInfo with ControlMacroLogMacro {
    def apply(controlMacroLog: c.Expr[ControlMacroLog]) = {
      import c.universe._
      val v: c.type = c

      if (controlMacroLog.needShow)
        showInfo("hello ssss")
      q"1"
    }
  }

}