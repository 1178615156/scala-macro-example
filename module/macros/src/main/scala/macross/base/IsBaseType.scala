package macross.base

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/25.
 */
trait IsBaseType {
  val c: Context

  import c.universe._

  def isBaseType(t: Tree) = t match {
    case e@tq"Int" => true
    case e@tq"Boolean" => true
    case e@tq"Long" => true
    case e@tq"String" => true
    case _ => false
  }

  def isBaseType(t: Type) = t match {
    case e if c.typeOf[Int]     <:< t => true
    case e if c.typeOf[Boolean] <:< t => true
    case e if c.typeOf[Long]    <:< t => true
    case e if c.typeOf[String]  <:< t => true
    case _ => false
  }
  def isOptionBaseType(t:Type) = t match {
    case e if c.typeOf[Option[Int]    ] <:< t => true
    case e if c.typeOf[Option[Boolean]] <:< t => true
    case e if c.typeOf[Option[Long]   ] <:< t => true
    case e if c.typeOf[Option[String] ] <:< t => true
    case _ => false
  }


  def isOptionBaseType(t: Tree) = t match {
    case e@tq"Option[Int]" => true
    case e@tq"Option[Boolean]" => true
    case e@tq"Option[Long]" => true
    case e@tq"Option[String]" => true
    case _ => false
  }

  def collectBaseType[A <: Tree, B >: Tree]: PartialFunction[A, B] = {
    case e@tq"Int" => e
    case e@tq"Boolean" => e
    case e@tq"Long" => e
    case e@tq"String" => e
  }

  def collectOptionBaseType[A <: Tree, B >: Tree]: PartialFunction[A, B] = {
    case e@tq"Option[Int]" => e
    case e@tq"Option[Boolean]" => e
    case e@tq"Option[Long]" => e
    case e@tq"Option[String]" => e
  }
}
