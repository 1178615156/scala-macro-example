package macross

import macross.base.ShowInfo

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2015/12/6.
  */
sealed trait ControlMacroLog


object ControlMacroLog {

  trait NeedShow extends ControlMacroLog

  trait unNeedShow extends ControlMacroLog

  implicit
  object default extends unNeedShow

}

trait ControlMacroLogMacro {
  val c: blackbox.Context

  import c.universe._

  implicit class WithAsShowInfo(controlMacroLog: c.Expr[ControlMacroLog]) {
    def needShow =
      controlMacroLog.tree.symbol.asTerm.info <:< typeOf[ControlMacroLog.NeedShow]

  }

}

private[macross] object ControlMacroLogTest {
  def apply(implicit controlMacroLog: ControlMacroLog): Any = macro Impl.apply

  class Impl(val c: blackbox.Context) extends ShowInfo with ControlMacroLogMacro {
    def apply(controlMacroLog: c.Expr[ControlMacroLog]) = {
      import c.universe._

      if (controlMacroLog.needShow)
        showInfo("hello ssss")
      q"1"
    }
  }

}