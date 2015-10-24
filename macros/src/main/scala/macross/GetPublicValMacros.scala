package macross

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/10.
 */

object GetPublicValMacros {
  /**
   *
   * @tparam ClassType
   * @tparam ReturnType if no write return type then
   *                    the compiler un not sure the return type
   *                    or use List[Any]
   * @return
   */
  def listValue[ClassType, ReturnType]: List[ReturnType] = macro GetPublicValMacrosImpl.listValueImpl[ClassType, ReturnType]

  def mapValue[ClassType, ReturnType]: Map[String,ReturnType] = macro GetPublicValMacrosImpl.mapValueImpl[ClassType, ReturnType]
}

/**
 * mast add val in the c:Context
 * because if no val then the c is private
 * @param c
 */
class GetPublicValMacrosImpl(val c: Context) {

  import c.universe._

  def getPublicVal(typ: c.Type) = {
    //get class inside all method
    typ.members.filter(_.isMethod).map(_.asMethod)
      //only get public
      .filter(_.isPublic)

  }

  def listValueImpl[ClassType: c.WeakTypeTag, ReturnType: c.WeakTypeTag]: c.Expr[List[ReturnType]] = {
    val typ: c.Type = c.weakTypeOf[ClassType]
    val rt = c.weakTypeOf[ReturnType]
    c.Expr[List[ReturnType]](
    // quasiquotes instructions :
    // http://docs.scala-lang.org/overviews/quasiquotes/intro.html
      q"""
    List(..${
        getPublicVal(typ)
          // filter return type is ReturnType
          .filter(_.info.resultType.<:<(rt))
          //if is a val then the value has getter method
          //but if is private[this] val will be not
          .filter(_.isGetter)
          .map(_.name)
          .toList.reverse
        //why need reverse
        //you guess
        /**
         * object TT{
         * val a=1
         * val b=2
         * val c=3
         *
         * when use macro
         * val list:List[Int]=GetPublicValMacros.apply[TT,Int]
         * then Equivalent to
         *
         * if no reverse then
         * val list=List(c,b,a) -- run stack is put a,b,c --pop c,b,a
         *
         * if has then
         * val list=List(a,b,c)
         */
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
