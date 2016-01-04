package macross.play

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.whitebox.Context

import java.io.{File, PrintWriter}

import macross.annotation.base.AnnotationParam
import macross.base.{ProjectFolder, ShowInfo}
import yjs.annotation.Routes.{Delete, Get, Post, Put}

/**
  * Created by yu jie shui on 2015/12/11 15:25.
  */
class MakeRoutes(path: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakeRoutesImpl.annotationImpl
}

trait RoutesFilePath
object MakeRoutes {


  def routesFilePath[T](path: String)(implicit routesFilePath: RoutesFilePath): Unit =
  macro MakeRoutesImpl.routesFilePathImpl[T]

  def apply[T](path: String): Unit =
  macro MakeRoutesImpl.pathImpl[T]
}

class MakeRoutesImpl(val c: Context)
  extends ProjectFolder
  with ShowInfo
  with AnnotationParam {

  private[this] case class RouteLine(HttpMethod: String, url: String, codeMethod: String, params: String = "") {
    def id = HttpMethod + url + codeMethod
  }

  import c.universe._

  private[this] def defaultRoutesFile: File = {
    //    config.getString("application.route")
    val of = Play.confOutputDir.listFiles().filter(e ⇒ e.getName == ("routes") || e.getName.contains(".routes"))
    if (of.length < 1)
      c.abort(c.enclosingPosition, s"not find routes file in ${Play.confOutputDir.getAbsolutePath}")
    else if (of.length > 1)
      c.abort(c.enclosingPosition, s"routes file too many ${of.map(_.getAbsolutePath)}")
    else
      of.head
  }


  private[this] def controllerRouteLines(controller: Symbol, path: String): Seq[RouteLine] = {
    val controllerMethod =
      controller.typeSignature.members
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
          case q"new ${annotation}(url= ${Literal(Constant(url: String))} )" ⇒ annotation → url
          case q"new ${annotation}(${Literal(Constant(url: String))} )" ⇒ annotation → url
        }.map { case (annotation, url) ⇒
          val httpMethod = annotation.tpe match {
            case e if e <:< typeOf[Get] ⇒ "GET"
            case e if e <:< typeOf[Post] ⇒ "POST"
            case e if e <:< typeOf[Put] ⇒ "PUT"
            case e if e <:< typeOf[Delete] ⇒ "DELETE"
          }
          RouteLine(
            HttpMethod = httpMethod,
            url = path + url,
            codeMethod =
              s"${if (controller.isModule || controller.isModuleClass) "" else "@"}${controller.fullName}.${method.name}",
            params =
              method.asMethod.paramLists.map(_.map(e => e.name.toString + ":" + e.info).mkString("(", ",", ")")).mkString
          )
        }
    }.toList
    controllerRouteLines
  }

  private[this] def fileRoutesLines(routesFile: File): Seq[RouteLine] = {
    val asRequestUrl =
      "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9|_]+) +(@|[a-z|A-Z|.|0-9|_]+)".r
    val asRequestUrlWithParams =
      "(GET|POST|DELETE|PUT|->) +([a-z|A-Z|/|0-9|_]+) +(@|[a-z|A-Z|.|0-9|_]+) ?(\\(.*\\))".r

    val routes = scala.io.Source.fromFile(routesFile).getLines()
    val fileRoutes: Seq[RouteLine] = routes.collect {
      case asRequestUrlWithParams(a, b, c, d) ⇒ RouteLine(a.trim, b.trim, c.trim, d.trim)
      case asRequestUrl(a, b, c) ⇒ RouteLine(a.trim, b.trim, c.trim)
    }.toList
    fileRoutes
  }


  private def impl(controller: Symbol, path: String, routesFile: Option[File] = None) = {
    val controllerRouteLines: Seq[RouteLine] = this.controllerRouteLines(controller, path)
    val fileRouteLines: Seq[RouteLine] = this.fileRoutesLines(routesFile.getOrElse(this.defaultRoutesFile))
    val fileRoutesMap = fileRouteLines
      .filterNot(e ⇒ controllerRouteLines.exists(_.url == e.url))
      .filterNot(e ⇒ controllerRouteLines.exists(_.codeMethod == e.codeMethod))
      .map(e ⇒ e.id → e).toMap

    val out = fileRoutesMap ++ controllerRouteLines.map(e ⇒ e.id → e).toMap

    val hasChange = !out.toList.map(_._2).forall(e ⇒ fileRouteLines.contains(e))

    val asDebug = false
    if (asDebug) {
      showInfo(
        s"""
           |file      : ${fileRouteLines}
           |out       : ${out.toList.map(_._2)}
           |hasChange : $hasChange
       """.stripMargin)
    }
    if (hasChange) {
      val maxUrlSize = out.values.toList.map(_.url.size).max
      val fileTxt =
        out.values.toList.sortBy(_.url).map(x ⇒ {
          s"${x.HttpMethod}${" " * (8 - x.HttpMethod.size)}${x.url}${" " * (maxUrlSize - x.url.size + 2)}${x.codeMethod}${x.params}"
        }).mkString("\n")

      val outRoutesFile = new PrintWriter(routesFile.getOrElse(this.defaultRoutesFile))
      outRoutesFile.print(fileTxt)
      outRoutesFile.close()

      showInfo("routes file = \n" + show(fileTxt))
    }

  }

  def annotationImpl(annottees: c.Expr[Any]*): c.Expr[Any] = {

    // get make routes path property
    val path = annotationParams.head.collect {
      case q"${Literal(Constant(path: String))}" ⇒ path
      case q"path=${Literal(Constant(path: String))}" ⇒ path
    }.head

    val controller: c.universe.Symbol = c.typecheck(annottees.head.tree).symbol

    impl(controller, path)

    c.Expr(q"{..${annottees}}")
  }

  def pathImpl[T: c.WeakTypeTag](path: c.Expr[String]) = {
    val controller: c.universe.Symbol = c.weakTypeOf[T].typeSymbol
    impl(controller, c.eval(path))
    q"""
        ()
      """
  }

  def routesFilePathImpl[T: c.WeakTypeTag](path: c.Expr[String])(routesFilePath: c.Expr[RoutesFilePath]) = {
    val controller: c.universe.Symbol = c.weakTypeOf[T].typeSymbol

    val folder = ".*`(.*)`".r
    impl(controller, c.eval(path), Some(new File(
      showCode(routesFilePath.tree) match {
        case folder(routesFile) ⇒ routesFile
      }
    )))
    q"""
        ()
      """
  }
}