package macross.slick.spi

import scala.reflect.macros.blackbox

/**
  * Created by yu jie shui on 2015/12/7 9:35.
  */
trait GetTableRepValue {
  val c: blackbox.Context

  import c.universe._

  def tableRepValue[EntityTable: c.WeakTypeTag]: Seq[c.universe.MethodSymbol] = {
    val et = c.weakTypeOf[EntityTable]
    val etMembers = et.members
      .filter(_.isMethod).map(_.asMethod)
      .filter(_.isPublic)
      .filter(_.returnType.finalResultType.typeConstructor =:= typeOf[slick.lifted.Rep[_]].typeConstructor)
      .filterNot(_.isConstructor)
      .filterNot(_.name.toString == "column")

    etMembers.toSeq
  }
}
