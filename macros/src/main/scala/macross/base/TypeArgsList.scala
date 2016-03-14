package macross.base

import scala.reflect.macros.blackbox

/**
  * Created by yujieshui on 2016/3/14.
  */
trait TypeArgsList {
  val c: blackbox.Context

  import c.universe._


  def getTypeList(it: Type): List[Type] =
    if (it.typeArgs.isEmpty)
      List(it)
    else
      it.typeConstructor +: it.typeArgs.flatMap(getTypeList)

  def typeParamsList[T: c.WeakTypeTag]: List[Type] = {
    getTypeList(c.weakTypeOf[T].dealias)
  }
}
