package yjs.macrs.play

import java.io.{File, PrintWriter}

import com.typesafe.config.ConfigFactory

import scala.io.Source
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by yuJieShui on 2016/7/15.
  */
trait ProjectProperty {
  val c: scala.reflect.macros.blackbox.Context

  final def config = ConfigFactory.load(getClass.getClassLoader)

  final def projectDir = config.getString("user.dir")

  final def currentPackage = c.internal.enclosingOwner.fullName

}

trait AnnotationUtils {
  val c: scala.reflect.macros.blackbox.Context

  import c.universe._

  def getAnnotation(x: MemberDef) = x.mods.annotations

  def getAnnotationValue(tree: Tree): Seq[String] = tree match {
    case q"new $name (..$values)" => values collect {
      case AssignOrNamedArg(Ident(TermName(_)), Literal(Constant(s: String))) => s
      case Literal(Constant(s: String))                                       => s
    }
  }

  def getAnnotationName(tree: Tree): String = tree match {
    case q"new $name (..$values)" => name.toString().split("\\.").last
  }
}

class MakeRouteMacroImpl(override val c: blackbox.Context) extends ProjectProperty
  with AnnotationUtils {

  import c.universe._

  private case class Rule(httpMethod: String, url: String, callMethod: String, params: String) {
    def id = httpMethod + url + callMethod

    override def toString: String = s"$httpMethod $url $callMethod $params"
  }

  private def typeAs[T: TypeTag](tree: Tree): Boolean = c.typecheck(tree).tpe <:< typeOf[T]

  private def controllerRule(x: MemberDef with ImplDef): Seq[Rule] = {
    val annotations = getAnnotation(x).filter(e => typeAs[Routes.Path](e))
    val methods = x.impl.body.collect { case defVal: DefDef => defVal }.filter(_.mods.annotations.exists(e => typeAs[Routes.Method](e)))

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
    rules
  }

  private def resolveRoutesFile(routesFile: File): Seq[Rule] = {
    val asRequestUrl =
      "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9|_]+) +([@|a-z|A-Z|.|0-9|_]+)".r
    val asRequestUrlWithParams =
      "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9|_]+) +([@|a-z|A-Z|.|0-9|_]+) ?(\\(.*\\))".r

    val lines = Source.fromFile(routesFile).getLines().toSeq

    lines collect {
      case asRequestUrl(httpMethod, url, callMethod) =>
        Rule(httpMethod, url, callMethod, "")

      case asRequestUrlWithParams(httpMethod, url, callMethod, params) =>
        Rule(httpMethod: String, url: String, callMethod: String, params: String)
    }
  }

  private def routesFile() = new File(projectDir + "/conf/routes")

  private def fileRoutesRule() = resolveRoutesFile(routesFile())

  private def ruleMap(seq: Seq[Rule]) = seq.map(e => e.id -> e).toMap

  private def ruleHasChange(controllerRule: Seq[Rule], fileRoutesRule: Seq[Rule]): Boolean = {
    val fileRoutesRuleMap = ruleMap(fileRoutesRule)
    val controllerRuleMap = ruleMap(controllerRule)
    fileRoutesRuleMap != controllerRuleMap
  }

  private def mergeRule(controllerRule: Seq[Rule], fileRoutesRule: Seq[Rule]): Seq[Rule] = {
    val other = fileRoutesRule
      .filterNot(e => controllerRule.exists(_.url == e.url))
      .filterNot(e => controllerRule.exists(_.callMethod == e.callMethod))

    other ++ controllerRule
  }

  def impl(annottees: Tree*): Tree = {

    annottees.foreach {
      case x: MemberDef with ImplDef =>
        val controllerRule: Seq[Rule] = this.controllerRule(x)
        val fileRoutesRule: Seq[Rule] = this.fileRoutesRule()
        if (ruleHasChange(controllerRule, fileRoutesRule)) {
          val finalRules = mergeRule(controllerRule, fileRoutesRule)
          val maxUrlSize = finalRules.map(_.url.length).max
          val fileTxt = finalRules
            .map(rule =>
              rule.httpMethod +
                (" " * (8 - rule.httpMethod.size)) +
                rule.url +
                (" " * (maxUrlSize - rule.url.size + 2)) +
                rule.callMethod +
                rule.params
            ).mkString("\n")
          val outRoutesFile = new PrintWriter(routesFile())
          outRoutesFile.print(fileTxt)
          outRoutesFile.close()
          c.echo(c.enclosingPosition,
            controllerRule.mkString("\n")
          )
        } else {
          //nothing to do
        }
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
    case x: Defn.Trait  => controllerRouteLines(x.templ.stats.getOrElse(Nil))
    case x: Defn.Class  => controllerRouteLines(x.templ.stats.getOrElse(Nil))
    case x: Defn.Object => controllerRouteLines(x.templ.stats.getOrElse(Nil))
  }

}