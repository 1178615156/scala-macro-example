package yjs.macrs.slick

import java.sql.ResultSet

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yujieshui on 2017/5/12.
  */
class WrapResultSet[T](val f: (ResultSet, String) => T)

object WrapResultSet {

  implicit val wrapInt   : WrapResultSet[Int]     = new WrapResultSet((rt: ResultSet, column: String) => rt.getInt(column))
  implicit val wrapLong  : WrapResultSet[Long]    = new WrapResultSet((rt: ResultSet, column: String) => rt.getLong(column))
  implicit val wrapString: WrapResultSet[String]  = new WrapResultSet((rt: ResultSet, column: String) => rt.getString(column))
  implicit val wrapDouble: WrapResultSet[Double]  = new WrapResultSet((rt: ResultSet, column: String) => rt.getDouble(column))
  implicit val wrapBool  : WrapResultSet[Boolean] = new WrapResultSet((rt: ResultSet, column: String) => rt.getBoolean(column))

  implicit val wrapOptionInt   : WrapResultSet[Option[Int]]     = new WrapResultSet((rt: ResultSet, column: String) => Option(rt.getInt(column)))
  implicit val wrapOptionLong  : WrapResultSet[Option[Long]]    = new WrapResultSet((rt: ResultSet, column: String) => Option(rt.getLong(column)))
  implicit val wrapOptionString: WrapResultSet[Option[String]]  = new WrapResultSet((rt: ResultSet, column: String) => Option(rt.getString(column)))
  implicit val wrapOptionDouble: WrapResultSet[Option[Double]]  = new WrapResultSet((rt: ResultSet, column: String) => Option(rt.getDouble(column)))
  implicit val wrapOptionBool  : WrapResultSet[Option[Boolean]] = new WrapResultSet((rt: ResultSet, column: String) => Option(rt.getBoolean(column)))

}

object GetResultBuild {
  def get[T](rt: ResultSet, column: String)(implicit wrapResultSet: WrapResultSet[T]): T = wrapResultSet.f(rt, column)

  def literal[T]: slick.jdbc.GetResult[T] = macro GetResultBuildImpl.literal[T]

}

class GetResultBuildImpl(val c: Context) {

  import c.universe._

  def literal[T: c.WeakTypeTag] = impl[T](e => e)

  def impl[T: c.WeakTypeTag](transport: String => String) = {
    val tpe = c.weakTypeOf[T]
    val constructorMethod = tpe.members.filter(_.isConstructor).head.asMethod
    val tempName = TermName("aaa")

    val params = constructorMethod.paramLists.map(_
      .map(e => e.name -> e.info)
      .map { case (name, t) => q"yjs.macrs.slick.GetResultBuild.get[$t]($tempName.rs,${transport(name.toString)})" }
    )
    val rt = q"slick.jdbc.GetResult[$tpe](($tempName :slick.jdbc.PositionedResult ) => new $tpe(...$params))"

    c.echo(c.enclosingPosition, rt.toString())
    rt
  }
}