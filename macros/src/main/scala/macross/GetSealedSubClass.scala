package macross

import scala.language.higherKinds
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/10/16.
 */

object GetSealedSubClass {
  def ol[T]: List[T] = macro GetSealedSubClassImpl.apply[T]
}

class GetSealedSubClassImpl(val c: Context) extends base.ShowInfo{

  import c.universe._

  def apply[T: c.WeakTypeTag] = {
    val sub = c.weakTypeOf[T].typeSymbol.asClass.knownDirectSubclasses
    val rt = q"List(..${sub.map(e ⇒ q"${e.asClass.module}")})"
    rt
  }

}