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
class SlickTupledAndUnapply(val showInfo: Boolean) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro SlickTupledAndUnapplyImpl.apply
}


class SlickTupledAndUnapplyImpl(val c: Context)
  extends GetInClass
  with ShowInfo
  with ClassWithFunc
  with IsBaseType
  with AnnotationParam
  with SlickTypeMacro.Replace {

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

    //    showInfo(show(paramsType.filter(isReplace)))
    val nameWithParamType = params.map(e => e.name -> e.tpt).collect {
      PartialFunction(ee => {
        ee._2 match {
          case tq"Int" => ee
          case tq"Boolean" => ee
          case tq"Long" => ee
          case tq"String" => ee

          case tq"Option[Int]" => ee
          case tq"Option[Boolean]" => ee
          case tq"Option[Long]" => ee
          case tq"Option[String]" => ee

          case tq"Future[String]" => ee
          case tq"Option[Future[String]]"=>ee
        }
      })
    }
    val filterParamTypes = paramsType.collect(
      collectBaseType orElse
        collectOptionBaseType orElse {
        vOrOption(tq"Future[_]")
      }
    )
    val filterParamTypeReplaces =
      paramsType.collect(
        collectBaseType orElse
          collectOptionBaseType orElse {
          vOrOption(tq"Future[_]").andThen(e => tq"String")
        }
      )


    showInfo(show(filterParamTypeReplaces))
    //with slickTupled func
    val slickTupled = q"""
        def slickTupled(ttt:(..${filterParamTypeReplaces}))={
        slickApply(
        ..${
      (1 to filterParamTypeReplaces.size)
        .map(e => TermName(s"_$e"))
        .zip(filterParamTypes).map(e => q"ttt.${e._1}:${e._2}")
    }
        )
        }
        """
    val slickUnapply = q"""
    def slickUnapply(a:${tq"${classDef.name}"})=Option(
    ..${nameWithParamType.map(_._1).map(e=>q"SlickType tran a.$e")}
    )

    """
    //        if (showInfoSwitch)
    showInfo(show(slickTupled))
    showInfo(show(slickUnapply))


    c.Expr[Any](Block(List(
      classDef,
      classWithFunc(moduleDef, List(slickApply, slickTupled,slickUnapply))),
      Literal(Constant(()))))

  }
}