package macross.annotation

import scala.annotation.StaticAnnotation
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

import macross.base.GetInClass

/**
  * Created by yujieshui on 2016/2/23.
  */
trait SyncFuture extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro SyncFutureImpl.impl
}

class SyncFutureImpl(val c: Context) extends GetInClass with base.ClassWithFunc {

  import c.universe._

  def getBody(tree: Tree): List[Tree] = {
    tree match {
      case q"$mod class $name (...$params) extends ..$bases {..$body} " => body: List[Tree]
      case q"$mod trait $name extends ..$bases {..$body} " => body: List[Tree]
    }
  }


  def impl(annottees: c.Expr[Any]*): Tree = {
    val __self = q"val __self = this"
    val body = getBody(annottees.head.tree)
    val bodyFunc = body
      .collect { case e: DefDef => e }
      .filterNot(e => e.mods.hasFlag(Flag.PRIVATE) || e.mods.hasFlag(Flag.PROTECTED))
      .map { case q"$mod def $name [..$ts] (...${_params}) = $body" =>
        val params: List[List[ValDef]] = _params
        q"$mod def $name [..$ts] (...$params) = __self.$name[..$ts](...${params.map(_.map(_.name))})"
      }
    println(
      showRaw(bodyFunc)
    )
    val syncClass =
      q"""
      class Sync{
        ..${bodyFunc}
      }
      """

    val syncFunc = q"val sync = new Sync"
    val result = classWithFunc(annottees.head.tree, List(
      __self, syncClass,syncFunc
    ))


    println(showRaw(
      result
    ))
    q" $result "

  }
}
