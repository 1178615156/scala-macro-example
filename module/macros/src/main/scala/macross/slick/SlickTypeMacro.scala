package macross.slick

import macross.base.IsBaseType

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/26.
 */
object SlickTypeMacro {

  //  trait Retention {
  //    self: IsBaseType =>
  //    val c: Context
  //
  //    import c.universe._
  //
  //    def isSlickRetentionType(params: List[ValDef]) = {
  //      params.filter(e => {
  //        isBaseType(e.tpt)
  //      })
  //    }
  //  }

  trait Replace {
    self: IsBaseType =>
    val c: Context

    import c.universe._

    val replaceList = List(
      tq"Future[_]" -> tq"String"
    )

    def vOrOption[A <: Tree, B >: Tree](tree: Tree): PartialFunction[A, B] = {
      case e@tq"$tree" => e
      case e@tq"Option[$tree]" => e
    }

    def isReplace(t: Tree) = {
      t match {
        case tq"Future[_]" => true
        case _ => false
      }
//      replaceList.map(_._1).flatMap(e => {
//        List(tq"$e", tq"Option[$e]")
//      }).exists(_.equalsStructure(t))
    }
  }

}
