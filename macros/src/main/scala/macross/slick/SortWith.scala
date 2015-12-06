package macross.slick

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import slick.lifted.ForeignKeyQuery


/**
  * Created by yu jie shui on 2015/11/8 19:09.
  */
trait SortByName {

  def applyPrefix[EntityTable](entityTable: EntityTable,
                               asc: Boolean, //
                               prefix: String)
  : PartialFunction[String, slick.lifted.Ordered] =
  macro SortByNameImpl.ShowInfo.applyPrefix[EntityTable]

  def apply[EntityTable](entityTable: EntityTable,
                         sortField: String,
                         asc: Boolean): slick.lifted.Ordered = macro SortByNameImpl.NoShowInfo.apply[EntityTable]

  def applyDebug[EntityTable](entityTable: EntityTable,
                              sortField: String,
                              asc: Boolean): slick.lifted.Ordered = macro SortByNameImpl.ShowInfo.apply[EntityTable]
}

object SortByName extends SortByName

trait SortByNameImpl extends macross.base.ShowInfo {
  val c             : Context
  val showInfoSwitch: Boolean

  import c.universe._

  def applyPrefix[EntityTable: c.WeakTypeTag](
                                               entityTable: c.Expr[EntityTable],
                                               asc: c.Expr[Boolean],
                                               prefix: c.Expr[String])
  : c.Expr[PartialFunction[String, slick.lifted.Ordered]] = {
    val et = c.weakTypeOf[EntityTable]
    val etMembers = et.members.filter(_.isPublic)
      .filter(_.isMethod)
      .map(_.asMethod)
      .filter(_.info.resultType <:< typeOf[slick.lifted.Rep[_]])
      .filter(e ⇒ !(e.info.resultType <:< typeOf[ForeignKeyQuery[_, _]]))
      .map(e => e.name.toString -> e.info.resultType)
      .filter(_._1 != "encodeRef")
      .filter(_._1 != "<init>")

    val nameCaseAsc = etMembers.map {
      case (name, typ) =>
        val caseName=TermName(c.freshName("slick_sort_by_name"))
        cq"""
              $caseName:String if ($caseName==($prefix+$name)) =>{
                $entityTable.${name: TermName}.asc
             }
            """
    }
    val nameCaseDesc = etMembers.map {
      case (name, typ) =>
        val caseName=TermName(c.freshName("slick_sort_by_name"))
        cq"""
              $caseName:String if ($caseName==($prefix+$name)) =>{
                $entityTable.${name: TermName}.desc
             }
            """
    }
    val rt =
      q"""
          {
          if ($asc)
            PartialFunction((name:String)=>{(name match {
               case ..$nameCaseAsc
             }):slick.lifted.Ordered})
          else
            PartialFunction((name:String)=>{(name match {
               case ..$nameCaseDesc
            }):slick.lifted.Ordered})
        }
        """
    if (showInfoSwitch)
      showInfo(show(rt))
    c.Expr[PartialFunction[String, slick.lifted.Ordered]](rt)
  }

  def apply[EntityTable: c.WeakTypeTag](
                                         entityTable: c.Expr[EntityTable],
                                         sortField: c.Expr[String],
                                         asc: c.Expr[Boolean]) = {
    q"""
      ${applyPrefix(entityTable, asc, c.Expr[String]( q""" "" """))}($sortField)
      """
  }
}

object SortByNameImpl {

  class NoShowInfo(val c: Context) extends SortByNameImpl {
    val showInfoSwitch = false
  }

  class ShowInfo(val c: Context) extends SortByNameImpl {
    val showInfoSwitch = true
  }

}