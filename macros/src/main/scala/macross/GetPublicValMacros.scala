package macross

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/10.
 */

object GetPublicValMacros {
  def listValue[ClassType, ReturnType]: List[ReturnType] = macro GetPublicValMacrosImpl.listValueImpl[ClassType, ReturnType]

  def mapValue[ClassType, ReturnType]: Map[String,ReturnType] = macro GetPublicValMacrosImpl.mapValueImpl[ClassType, ReturnType]
}

class GetPublicValMacrosImpl(val c: Context) {

  import c.universe._

  def getPublicVal(typ: c.Type) = {
    typ.members.filter(_.isMethod).map(_.asMethod)
      .filter(_.isPublic)

  }

  def listValueImpl[ClassType: c.WeakTypeTag, ReturnType: c.WeakTypeTag]: c.Expr[List[ReturnType]] = {
    val typ: c.Type = c.weakTypeOf[ClassType]
    val rt = c.weakTypeOf[ReturnType]
    c.Expr[List[ReturnType]](
      q"""
    List(..${
        getPublicVal(typ)
          .filter(_.info.resultType.<:<(rt))
          .filter(_.isGetter)
          .map(_.name)
          .toList.reverse
      }).asInstanceOf[List[$rt]].filter(_!=null)
    """
    )
  }

  def mapValueImpl[ClassType: c.WeakTypeTag, ReturnType: c.WeakTypeTag]: c.Expr[Map[String, ReturnType]] = {
    val typ = c.weakTypeOf[ClassType]
    val rt = c.weakTypeOf[ReturnType]
    c.Expr[Map[String, ReturnType]]( q"""
    Map(..${
      getPublicVal(typ)
        .filter(_.info.resultType.<:<(rt))
        .filter(_.isGetter)
        .map(e => e.name.toString -> e.name)
    }).asInstanceOf[Map[String,$rt]].filter(_._2!=null)
    """)
  }
}
