package macross.play

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import java.io.PrintWriter

import macross.annotation.base.AnnotationParam
import macross.base.{ConfFolder, ShowInfo}
import yjs.annotation.Routes.{Delete, Put, Get, Post}

/**
  * Created by yu jie shui on 2015/12/11 15:25.
  */
class MakeRoutes(path: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakeRoutesImpl.apply
}

class MakeRoutesImpl(val c: Context)
  extends ConfFolder
  with ShowInfo
  with AnnotationParam {

  private[this] case class RouteLine(HttpMethod: String, url: String, codeMethod: String, params: String = "") {
    def id = HttpMethod + url + codeMethod
  }

  import c.universe._

  private[this] val routesFile = {
    //    config.getString("application.route")
    val of = Play.confOutputDir.listFiles().filter(e ⇒ e.getName == ("routes") || e.getName.contains(".Routes"))
    if (of.length < 1)
      c.abort(c.enclosingPosition, s"not find routes file in ${Play.confOutputDir.getAbsolutePath}")
    else if (of.length > 1)
      c.abort(c.enclosingPosition, s"routes file too many ${of.map(_.getAbsolutePath)}")
    else
      of.head
  }

  private[this] def controllerRouteLines(controller: Symbol, path: String): Seq[RouteLine] = {
    val controllerMethod = controller.typeSignature.members
      .filter(e ⇒ e.annotations.nonEmpty)
      .map(e ⇒
        (e, e.annotations.filter(e ⇒
          e.tree.tpe <:< typeOf[Get] ||
            e.tree.tpe <:< typeOf[Post] ||
            e.tree.tpe <:< typeOf[Put] ||
            e.tree.tpe <:< typeOf[Delete]
        )
          ))
      .filter(_._2.nonEmpty)

    val controllerRouteLines: Seq[RouteLine] = controllerMethod.flatMap {
      case (method: c.universe.Symbol, annotations: List[c.universe.Annotation]) ⇒
        annotations.map(_.tree).map {
          case q"new ${annotation}(url = ${Literal(Constant(url: String))} )" ⇒
            val httpMethod = annotation.tpe match {
              case e if e <:< typeOf[Get] ⇒ "GET"
              case e if e <:< typeOf[Post] ⇒ "POST"
              case e if e <:< typeOf[Put] ⇒ "PUT"
              case e if e <:< typeOf[Delete] ⇒ "DELETE"

            }
            RouteLine(
              HttpMethod = httpMethod,
              url = path + url, codeMethod = method.fullName,
              params =
                  method.asMethod.paramLists.map(_.map(e => e.name.toString + ":" + e.info).mkString("(", ",", ")")).mkString
            )
        }
    }.toList
    controllerRouteLines
  }

  private[this] def fileRoutesLines(controller: Symbol, path: String): Seq[RouteLine] = {
    val routes = scala.io.Source.fromFile(routesFile).getLines()
    val fileRoutes: Seq[RouteLine] = {
      val asRequestUrl = "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9]+) +([a-z|A-Z|.|0-9]+)".r
      val asRequestUrlWithParams = "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9]+) +([a-z|A-Z|.|0-9]+) ?(\\(.*\\))".r
      routes.collect {
        case asRequestUrlWithParams(a, b, c, d) ⇒ RouteLine(a.trim, b.trim, c.trim, d.trim)
        case asRequestUrl(a, b, c) ⇒ RouteLine(a.trim, b.trim, c.trim)
      }.toList
    }
    fileRoutes
  }

  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
    val asDebug = false
//    val asDebug = true
    // get make routes path property
    val path = annotationParams.head.collect {
      case q"${Literal(Constant(path: String))}" ⇒ path
      case q"path=${Literal(Constant(path: String))}" ⇒ path
    }.head
    //get controller class inside need make url method
    val controller: c.universe.Symbol = c.typecheck(annottees.head.tree).symbol
    val controllerRouteLines: Seq[RouteLine] = this.controllerRouteLines(controller, path)

    val fileRouteLines: Seq[RouteLine] = this.fileRoutesLines(controller, path)
    //    showInfo(""+show(fileRouteLines))
    val fileRoutesMap = fileRouteLines
      .filterNot(e ⇒ controllerRouteLines.exists(_.url == e.url))
      .filterNot(e ⇒ controllerRouteLines.exists(_.codeMethod == e.codeMethod))
      .map(e ⇒ e.id → e).toMap

    val out = fileRoutesMap ++ controllerRouteLines.map(e ⇒ e.id → e).toMap

    val hasChange = !out.toList.map(_._2).forall(e ⇒ fileRouteLines.contains(e))

    if (asDebug)
      showInfo(
        s"""
           |file      : ${fileRouteLines}
           |out       : ${out.toList.map(_._2)}
           |hasChange : $hasChange
       """.stripMargin)
    if (hasChange) {
      val maxUrlSize = out.values.toList.map(_.url.size).max
      val fileTxt =
        out.values.toList.sortBy(_.url).map(x ⇒ {
          s"${x.HttpMethod}${" " * (8 - x.HttpMethod.size)}${x.url}${" " * (maxUrlSize - x.url.size + 2)}${x.codeMethod}${x.params}"
        }).mkString("\n")

      val outRoutesFile = new PrintWriter(routesFile)
      outRoutesFile.print(fileTxt)
      outRoutesFile.close()

      showInfo("routes file = \n" + show(fileTxt))
    }
    c.Expr(q"{..${annottees}}")
  }
}