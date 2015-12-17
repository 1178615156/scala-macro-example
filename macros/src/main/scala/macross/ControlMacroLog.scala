package macross

import macross.base.ShowInfo

import scala.language.experimental.macros
import scala.reflect.macros.blackbox
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2015/12/6.
  */

trait ControlMacroLog {

  trait Value

  trait NeedShow extends Value

  trait UnNeedShow extends Value

}

object ControlMacroLog extends ControlMacroLog {

  implicit object default extends NeedShow

}

trait ControlMacroLogMacro {
  val c: blackbox.Context

  import c.universe._

  implicit class WithAsShowInfo2(controlMacroLog: c.Expr[ControlMacroLog.Value]) {
    def needShow =
      controlMacroLog.tree.symbol.asTerm.info <:< typeOf[ControlMacroLog.NeedShow]

  }

}


private[macross] object ControlMacroLogTest {
  def apply(implicit controlMacroLog: ControlMacroLog.Value): Any = macro Impl.apply

  class Impl(val c: blackbox.Context) extends ShowInfo with ControlMacroLogMacro {
    def apply(controlMacroLog: c.Expr[ControlMacroLog.Value]) = {
      import c.universe._

      showInfo(show(controlMacroLog.needShow))
      if (controlMacroLog.needShow)
        showInfo("hello ssss")
      q"1"
    }
  }

}