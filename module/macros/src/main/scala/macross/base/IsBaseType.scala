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
class L[-A]{
  def a(a: A)=1
}
//  val s: L[Option[Int]] = new L[Option[Int]]
  val l: L[Some[Int]] =  new L[Option[Int]]
  l.a(Some(1))
//  l.a(Option(1))
  def collectBaseType[A<:Tree, B>:Tree]: PartialFunction[A, B] =  {
    case e@tq"Int" => e
    case e@tq"Boolean" => e
    case e@tq"Long" => e
    case e@tq"String" => e
  }
  def collectOptionBaseType[A<:Tree, B>:Tree]: PartialFunction[A, B]={
    case e@tq"Option[Int]" => e
    case e@tq"Option[Boolean]" => e
    case e@tq"Option[Long]" => e
    case e@tq"Option[String]" => e
  }
}
