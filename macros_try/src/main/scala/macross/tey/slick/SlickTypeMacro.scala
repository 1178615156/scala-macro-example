//package macross.teach.slick
//
//import macross.base.IsBaseType
//
//import scala.concurrent.Future
//import scala.reflect.macros.blackbox.Context
//
///**
// * Created by YuJieShui on 2015/9/26.
// */
//object SlickTypeMacro {
//
//  trait Retention {
//    self: IsBaseType with Replace =>
//    val c: Context
//
//    import c.universe._
//
//    def isSlickRetentionType(params: Tree) = {
//      isBaseType(params) || isOptionBaseType(params) || isSlickReplaceType(params)
//    }
//
//    def isSlickRetentionType(params: Type) = {
//      isBaseType(params) || isOptionBaseType(params) || isSlickReplaceType(params)
//    }
//  }
//
//  trait Replace {
//    self: IsBaseType =>
//    val c: Context
//
//    import c.universe._
//
//    val replaceList = List(
//      tq"Future[String]" -> tq"String",
//      tq"DateTime" -> tq"String",
//    tq"org.joda.time.DateTime"→tq"String"
//    ).flatMap(e => List(e, tq"Option[${e._1}]" -> tq"Option[${e._2}]"))
//    val replaceMap = {
//      class RM {
//        def get(t: Tree): Option[c.universe.Tree] = {
//          replaceList.find(_._1.equalsStructure(t)).map(_._2)
//        }
//      }
//      new RM()
//    }
//
//    def isSlickReplaceType(t: Tree) = {
//      replaceList.exists(e => e._1.equalsStructure(t))
//    }
//
//    def replaceTo(t: Tree) = {
//      val replaceMap = {
//        class RM {
//          def get(t: Tree): Option[c.universe.Tree] = {
//            replaceList.find(_._1.equalsStructure(t)).map(_._2)
//          }
//        }
//        new RM()
//      }
//      replaceMap.get(t).getOrElse(t)
//    }
//
//    val replaceListType = List(
//      c.typeOf[Future[String]] -> c.typeOf[String]
//    )
//
//    def isSlickReplaceType(t: Type) =
//      replaceListType.exists(e => e._1 =:= t)
//
//    def replaceTo(t: Type) =
//      replaceListType.find(_._1 =:= t).map(_._2)
//
//  }
//
//}
