package macross.slick

import macross.annotation.base.{AnnotationParam, ClassWithFunc}
import macross.base.{IsBaseType, GetInClass, ShowInfo}

import scala.concurrent.Future
import scala.reflect.macros.blackbox.Context
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}


/**
 * Created by YuJieShui on 2015/9/24.
 */
class SlickTupled(val showInfo: Boolean) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro SlickTupledImpl.apply
}


class SlickTupledImpl(val c: Context)
  extends GetInClass
  with ShowInfo
  with ClassWithFunc
  with IsBaseType
  with AnnotationParam {

  import c.universe._


  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
    //get annotation param showInfo and check
    //if is true then show info in the compile
    val showInfoSwitch = annotationParam(TermName("showInfo")).equalsStructure(q"true")

    val classDef = getInClass(annottees.map(_.tree))
    val moduleDef = getInModule(annottees.map(_.tree))

    //get params
    val params: List[ValDef] = classDef match {
      case q"$med class $name (..$params) extends ..$base {..$body}" => params
    }

    //if has slickApply then use
    //else make a slickApply
    val hasSlickApply = moduleDef.impl.body.collect {
      case q"def slickApply($p):$t = {$impl}" => true
      case q"def slickApply:$t={$impl}" => true
    }.nonEmpty

    val slickApply =
      if (hasSlickApply) q"" else q"def slickApply=apply _ "
    //    if (sis) showInfo(hasSlickApply.toString())

    def vOrOption[A <: Tree, B >: Tree](tree: Tree): PartialFunction[A, B] = {
      case e@tq"$tree" => e
      case e@tq"Option[$tree]" => e
    }
    val paramsType = params.map(_.tpt)
    val a = paramsType.collect(
      collectBaseType orElse
        collectOptionBaseType orElse {
        vOrOption(tq"Future[_]").andThen(e => tq"String")
      }
    )

    showInfo(show(a))
    //with slickTupled func
    val slickTupled = q"""
        def slickTupled(ttt:(..${paramsType}))=slickApply(
        ..${(1 to paramsType.size).map(e => TermName(s"_$e")).map(e => q"ttt.$e")}
        )

        """

    if (showInfoSwitch) showInfo(show(slickTupled))


    c.Expr[Any](Block(List(
      classDef,
      classWithFunc(moduleDef, List(slickApply, slickTupled))),
      Literal(Constant(()))))

  }
}