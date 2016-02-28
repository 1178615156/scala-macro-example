package macross.annotation

import scala.annotation.StaticAnnotation
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

import macross.base.GetInClass

/**
  * Created by yujieshui on 2016/2/23.
  */
class SyncApi[T](f: T) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro SyncApiImpl.impl
}

class SyncApiImpl(val c: Context)
  extends GetInClass
    with base.ClassWithFunc
    with base.AnnotationParam
    with macross.base.ShowInfo {

  import c.universe._

  def getBody(tree: Tree): List[Tree] = {
    tree match {
      case q"$mod class $name[..$t] (...$params) extends ..$bases {..$body} " => body: List[Tree]
      case q"$mod trait $name[..$t] extends ..$bases {..$body} " => body: List[Tree]
    }
  }

  def getTs(tree: Tree): List[Tree] = {
    tree match {
      case q"$mod class $name[..$t] (...$params) extends ..$bases {..$body} " => t: List[Tree]
      case q"$mod trait $name[..$t] extends ..$bases {..$body} " => t: List[Tree]
    }
  }

  def impl(annottees: c.Expr[Any]*): Tree = {
    val __self = q"def  __self = {this}"
    val mapFunc = annotationParams.head.head

    val body = getBody(annottees.head.tree)
    val bodyFunc = body
      .collect { case e: DefDef => e }
      .filterNot(e => e.mods.hasFlag(Flag.PRIVATE) || e.mods.hasFlag(Flag.PROTECTED))
      .collect {

        case q"$mod def $name [..$ts] (...${_params}) : $rt = $body" =>
          (mod, name, ts, _params.asInstanceOf[List[List[ValDef]]], Some(rt))
        case q"$mod def $name [..$ts] (...${_params})  = $body" =>
          (mod, name, ts, _params.asInstanceOf[List[List[ValDef]]], None)

        // no body
        case q"$mod def $name [..$ts] (...${_params}) : $rt " =>
          (mod, name, ts, _params.asInstanceOf[List[List[ValDef]]], Some(rt))
      }.map {
      case (mod, name, ts, params, funcType) =>
//        DefDef(mod, name, ts, params,
//          funcType.getOrElse(EmptyTree)
//          , q"{$mapFunc.apply( __self.$name[..$ts](...${params.map(_.map(_.name))}))}")
      //        q"$mod def ${TermName("sync_" +name.toString())} [..$ts] (...$params) = {$mapFunc.apply( __self.$name[..$ts](...${params.map(_.map(_.name))}))}"
              q"""
$mod def ${TermName("sync_"+name.toString)} [..$ts] (...$params) =
{$mapFunc.apply( this.$name[..$ts](...${params.map(_.map(_.name))}))}
                  """
    }
    new {
    }
    showInfo(show(
      bodyFunc
    ))

    val ts = getTs(annottees.head.tree)
    val syncClass =
      q"""
      class Sync{
        ..${bodyFunc}
      }
      """
    //def get(key:Key) = {$mapFunc.apply(__self.get(key))}
    /*
        object sync {
        ..${bodyFunc}
        }
     */
    val syncFunc =
      q"""
          def sync = new Sync

      """
//    val result = classWithFunc(annottees.head.tree, List(
//      __self//,syncClass//, syncFunc
//    )++ bodyFunc)

    val result = annottees.head.tree match {
      case q"$mod trait $name[..$t] extends ..$base {..$body}"=>
        q"""
            $mod trait $name[..$t] extends ..$base {

            ..$body
            ${__self}
            ..${bodyFunc}
            }
            """
    }
    showInfo(show(
      result
    ))



    q" $result "

  }
}
