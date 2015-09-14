package macros.annotation.base

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by yu jie shui on 2015/9/14 15:14.
 */

trait FullNameBase {
   val c: Context

   import c.universe._

  def fullName(inClass:ClassDef)= c.typecheck(inClass).symbol.fullName


}