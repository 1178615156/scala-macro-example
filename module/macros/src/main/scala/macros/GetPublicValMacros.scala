package macros

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}

/**
 * Created by YuJieShui on 2015/9/10.
 */

//object GetPublicValMacros {
//  def apply[ClassType, ReturnType]: List[ReturnType] = macro GetPublicValMacrosImpl.impl[ClassType, ReturnType]
//}
//
///**
// * mast add val in the c:Context
// * because if no val then the c is private
// * @param c
// */
//class GetPublicValMacrosImpl(val c: Context) {
//
//  import c.universe._
//
//  def impl[ClassType: c.WeakTypeTag, ReturnType: c.WeakTypeTag] = {
//
//  }
//}
