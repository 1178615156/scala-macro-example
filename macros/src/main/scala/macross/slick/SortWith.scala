package macross.slick

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import macross.{ControlMacroLogMacro, ControlMacroLog}
import slick.lifted.ForeignKeyQuery


/**
  * Created by yu jie shui on 2015/11/8 19:09.
  */

trait SortByName {


  def applyPrefix[EntityTable](entityTable: EntityTable,
                               asc: Boolean, //
                               prefix: String)(
                                implicit controlMacroLog: ControlMacroLog
                              )
  : PartialFunction[String, slick.lifted.Ordered] = macro SortByNameImpl.applyPrefix[EntityTable]

  def apply[EntityTable](entityTable: EntityTable,
                         sortField: String,
                         asc: Boolean)
                        (implicit controlMacroLog: ControlMacroLog)
  : slick.lifted.Ordered = macro SortByNameImpl.apply[EntityTable]

  @deprecated
  def applyDebug[EntityTable](entityTable: EntityTable,
                              sortField: String,
                              asc: Boolean)
                             (implicit controlMacroLog: ControlMacroLog)
  : slick.lifted.Ordered = macro SortByNameImpl.apply[EntityTable]
}

object SortByName extends SortByName

class SortByNameImpl(val c: Context)
  extends macross.base.ShowInfo
  with spi.GetTableRepValue
  with ControlMacroLogMacro {

  import c.universe._

  def applyPrefix[EntityTable: c.WeakTypeTag](
                                               entityTable: c.Expr[EntityTable],
                                               asc: c.Expr[Boolean],
                                               prefix: c.Expr[String])
                                             (controlMacroLog: c.Expr[ControlMacroLog])
  : c.Expr[PartialFunction[String, slick.lifted.Ordered]] = {
    val etMembers = tableRepValue[EntityTable]
      .map(e => e.name.toString -> e.info.resultType)

    val nameCaseAsc = etMembers.map {
      case (name, typ) =>
        val caseName = TermName(c.freshName("slick_sort_by_name"))
        cq"""
              $caseName:String if ($caseName==($prefix+$name)) =>{
                $entityTable.${name: TermName}.asc
             }
            """
    }
    val nameCaseDesc = etMembers.map {
      case (name, typ) =>
        val caseName = TermName(c.freshName("slick_sort_by_name"))
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
    if (controlMacroLog.needShow) showInfo(show(rt))

    c.Expr[PartialFunction[String, slick.lifted.Ordered]](rt)
  }

  def apply[EntityTable: c.WeakTypeTag](
                                         entityTable: c.Expr[EntityTable],
                                         sortField: c.Expr[String],
                                         asc: c.Expr[Boolean])(
                                         controlMacroLog: c.Expr[ControlMacroLog]
                                       ) = {
    q"""
      ${applyPrefix(entityTable, asc, c.Expr[String]( q""" "" """))(controlMacroLog)}($sortField)
      """
  }
}