package macross.slick

import macross.base.IsBaseType

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/26.
 */
object SlickTypeMacro {

  trait Retention {
    self: IsBaseType with Replace =>
    val c: Context

    import c.universe._

    def isSlickRetentionType(params: Tree) = {
      isBaseType(params) || isOptionBaseType(params) || isSlickReplaceType(params)
      //      params match {
      //        case tq"Int" => true
      //        case tq"Boolean" => true
      //        case tq"Long" => true
      //        case tq"String" => true
      //        case tq"Option[Int]" => true
      //        case tq"Option[Boolean]" => true
      //        case tq"Option[Long]" => true
      //        case tq"Option[String]" => true
      //        case _ => false
      //      }
    }
  }

  trait Replace {
    self: IsBaseType =>
    val c: Context

    import c.universe._

    val replaceList = List(
      tq"Future[String]" -> tq"String"

    ).flatMap(e => List(e, tq"Option[${e._1}]" -> tq"Option[${e._2}]"))
    val replaceMap = {
      class RM {
        def get(t: Tree): Option[c.universe.Tree] = {
          replaceList.find(_._1.equalsStructure(t)).map(_._2)
        }
      }
      new RM()
    }

    def isSlickReplaceType(t: Tree) = {
      replaceList.exists(e => e._1.equalsStructure(t))
    }
  }

}
