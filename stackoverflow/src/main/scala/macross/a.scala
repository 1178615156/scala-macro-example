package macross

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/**
 * Created by YuJieShui on 2015/11/2.
 */
class ATypeParam[T] {
  def apply:T = macro AImpl.apply[T]
}

class AImpl(val c: whitebox.Context) extends base.ShowInfo {

  import c.universe._

  def apply[T: c.WeakTypeTag] = {
    showInfo(show(c.weakTypeOf[T]))
    q"1"
  }
}
