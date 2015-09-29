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
  with AnnotationParam
  with IsBaseType
  with SlickTypeMacro.Replace
  with SlickTypeMacro.Retention {

  import c.universe._

  def makeSlickTupled(classDef: c.universe.ClassDef, moduleDef: c.universe.ModuleDef) = {
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
    val paramsType = params.map(_.tpt)
    //    showInfo(show(filterParamTypeReplaces))
    //with slickTupled func
    val retentionType = paramsType.filter(isSlickRetentionType).map(e => if (isSlickReplaceType(e)) replaceMap.get(e).get else e)
    val slickTupled = q"""
        def slickTupled(ttt:(..${retentionType}))={
        slickApply(
        ..${
      (1 to retentionType.size)
        .map(e => TermName(s"_$e"))
        .map(e => q"ttt.${e}")
    }
        )
        }
        """
    List(slickApply, slickTupled)
  }

  def makeSlickUnapply(classDef: c.universe.ClassDef, moduleDef: c.universe.ModuleDef) = {
    //get params
    val params: List[ValDef] = classDef match {
      case q"$med class $name (..$params) extends ..$base {..$body}" => params
    }
    val nameWithParamType = params.map(e => e.name -> e.tpt).filter(e => {
      isSlickRetentionType(e._2)
    })

    val slickUnapply = q"""
    def slickUnapply(a:${tq"${classDef.name}"})=Option(
    ..${nameWithParamType.map(_._1).map(e => q"SlickType tran a.$e")}
    )
    """
    List(slickUnapply)
  }

  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
    //get annotation param showInfo and check
    //if is true then show info in the compile
    val showInfoSwitch = annotationParam(TermName("showInfo")).equalsStructure(q"true")

    val classDef: c.universe.ClassDef = getInClass(annottees.map(_.tree))
    val moduleDef: c.universe.ModuleDef = getInModule(annottees.map(_.tree))

//    //get params
//    val params: List[ValDef] = classDef match {
//      case q"$med class $name (..$params) extends ..$base {..$body}" => params
//    }
//    val paramsType = params.map(_.tpt)
//    showInfo(show(paramsType.filter(isSlickRetentionType)))

    val slickTupled = makeSlickTupled(classDef, moduleDef)
    val slickUnapply = makeSlickUnapply(classDef, moduleDef)
    if (showInfoSwitch) {
      showInfo(show(slickTupled))
      showInfo(show(slickUnapply))
    }


    c.Expr[Any](Block(List(
      classDef,
      classWithFunc(moduleDef, slickTupled ++ slickUnapply)),
      Literal(Constant(()))))

  }
}