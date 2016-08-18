package yjs.macrs.play

import scala.language.experimental.macros

/**
  * Created by yuJieShui on 2016/7/15.
  */

class MakeRouteMacroImpl(val c: scala.reflect.macros.blackbox.Context) {

  import c.universe._

  case class Rule(httpMethod: String, url: String, callMethod: String, params: String) {
    def id = httpMethod + url + callMethod
  }

  def getAnnotation(x:MemberDef)= x.mods.annotations

  def getAnnotationValue(tree: Tree): Seq[String] = tree match {
    case q"new $name (..$values)" => values collect {
      case AssignOrNamedArg(Ident(TermName(_)), Literal(Constant(s: String))) => s
      case Literal(Constant(s: String)) => s
    }
  }

  def getAnnotationName(tree: Tree) = tree match {
    case q"new $name (..$values)" => name.toString().split("\\.").last
  }

  def typeAs[T: TypeTag](tree: Tree): Boolean = c.typecheck(tree).tpe <:< typeOf[T]

  def mkRoute(x: MemberDef with ImplDef): Seq[Rule] = {
    val annotations = getAnnotation(x).filter(e => typeAs[Routes.Path](e))
    val methods= x.impl.body.collect { case defVal: DefDef => defVal }.filter(_.mods.annotations.exists(e => typeAs[Routes.Method](e)))

    val rules = for {
      path <- annotations map getAnnotationValue map (_.head)
      method <- methods
      (httpMethod, url) <- getAnnotation(method) map (e => (getAnnotationName(e), getAnnotationValue(e).head))
    } yield Rule(
      httpMethod = httpMethod.toUpperCase(),
      url = path + url,
      callMethod = currentPackage + "." + x.name + "." + method.name,
      params = method.vparamss.map(_.map(e => e.name + ":" + e.tpt).mkString("(", ",", ")")).mkString("")
    )

    println(rules)
    rules
  }

  def currentPackage = c.internal.enclosingOwner.fullName

  def impl(annottees: Tree*): Tree = {
    annottees.foreach {
      case x: ClassDef => mkRoute(x)
      case x: ModuleDef => mkRoute(x)
    }
    q"..${annottees}"
  }

}


class MakeRoute extends scala.annotation.StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakeRouteMacroImpl.impl
}


//todo wait impl
object MakeRoute {
  //  inline def apply(defn: Any) = meta {
  //  import MakeRoute._
  //     println(defn)
  //     println(Type.fresh())
  //    defn match {
  //      case q"..$mod class $name extends ..$base {..$body}" =>
  //        println(mod)
  //    }
  //
  //    defn
  //  }
  import scala.meta._

  def controllerRouteLines(stats: Seq[Stat]) = {
    stats.collect {
      case x: Defn.Def => x.mods.collect { case Mod.Annot(annot) => annot }
    }
  }

  def impl(defn: Any) = defn match {
    case x: Defn.Trait => controllerRouteLines(x.templ.stats.getOrElse(Nil))
    case x: Defn.Class => controllerRouteLines(x.templ.stats.getOrElse(Nil))
    case x: Defn.Object => controllerRouteLines(x.templ.stats.getOrElse(Nil))
  }

}