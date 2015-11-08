//package macross.teach.slick
//
//import scala.annotation.StaticAnnotation
//import macross.annotation.base.{AnnotationParam, ClassWithFunc}
//import macross.base.{IsBaseType, GetInClass, ShowInfo}
//
//import scala.concurrent.Future
//import scala.reflect.macros.blackbox.Context
//import scala.reflect.macros.blackbox.Context
//import scala.language.experimental.macros
//import scala.annotation.{compileTimeOnly, StaticAnnotation}
//
///**
// * Created by YuJieShui on 2015/10/11.
// */
//
//class SlickTupledUnapply[Value](val showInfo: Boolean) extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro SlickTUImpl.apply
//
//}
//
//class SlickTUImpl(val c: Context)
//  extends GetInClass
//  with ShowInfo
//  with ClassWithFunc
//  with AnnotationParam
//  with IsBaseType
//  with SlickTypeMacro.Replace
//  with SlickTypeMacro.Retention {
//
//  import c.universe._
//
//  def makeSlickTupled(params: List[c.universe.Symbol]) = {
//
//
//    val paramsType: List[c.universe.Type] = params.map(_.info)
////    showInfo(show(paramsType.filter(isSlickRetentionType)))
//    //with slickTupled func
//    val retentionType = paramsType.filter(isSlickRetentionType)
//      .map(e=> replaceTo(e).getOrElse(e))
////    showInfo(show(retentionType))
//    val slickTupled = q"""
//        def slickTupled(ttt:(..${retentionType}))={
//        slickApply(
//        ..${
//      (1 to retentionType.size)
//        .map(e => TermName(s"_$e"))
//        .map(e => q"ttt.${e}")
//    }
//        )
//        }
//        """
////    showInfo(show(slickTupled))
//    List(slickTupled)
//  }
//
//  def makeSlickUnapply(params: List[c.universe.Symbol]) = {
//
//    val nameWithParamType = params.map(e => e.name -> e.info).filter(e => {
//      isSlickRetentionType(e._2)
//    })
//
//    val slickUnapply = q"""
//    def slickUnapply(a:${tq"Value"})=Option(
//    ..${nameWithParamType.map(_._1).map(e => q"SlickType tran a.${e.toTermName}")}
//    )
//    """
////    showInfo(show(slickUnapply))
//    List(slickUnapply)
//  }
//
//  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
//    //get annotation param showInfo and check
//    //if is true then show info in the compile
//    val showInfoSwitch = annotationParam(TermName("showInfo")).equalsStructure(q"true")
//    val paramLists: List[List[c.universe.Symbol]] = c.macroApplication match {
//      case q"new $name (..$param).$fn(..$bn)" =>
//        val n: AppliedTypeTree = name.asInstanceOf[AppliedTypeTree]
//        c.mirror.staticClass(c.typecheck(q"${n.args.head.toString(): TermName}").symbol.fullName)
//          .asClass.toType
//          .member("<init>": TermName).asMethod.paramLists
//    }
////    showInfo(show(paramLists.head.map(e=>e.name->e.info)))
//
//
//    val classDef: c.universe.ClassDef = getInClass(annottees.map(_.tree)).head
//
//    val slickTupled = makeSlickTupled(paramLists.head)
//    val slickUnapply = makeSlickUnapply(paramLists.head)
//    if (showInfoSwitch) {
//      showInfo(show(slickTupled))
//      showInfo(show(slickUnapply))
//    }
//
//
//    c.Expr[Any](Block(List(
//      classWithFunc(classDef, slickTupled ++ slickUnapply)),
//      Literal(Constant(()))))
//
//  }
//}
