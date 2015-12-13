package macross.play

import macross.base.ConfFolder

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context
import yjs.annotation.Routes._
import java.io.{File, PrintWriter}

/**
  * Created by yu jie shui on 2015/12/11 15:25.
  */
class MakeRoutes(path: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakeRoutesImpl.apply
}

class MakeRoutesImpl(val c: Context)
  extends ConfFolder
  with macross.base.ShowInfo
  with macross.annotation.base.AnnotationParam {

  val routesFile = {
    //    config.getString("application.route")
    val of = Play.confOutputDir.listFiles().filter(e ⇒ e.getName == ("routes") || e.getName.contains(".Routes"))
    if (of.length < 1)
      c.abort(c.enclosingPosition, s"not find routes file in ${Play.confOutputDir.getAbsolutePath}")
    else if (of.length > 1)
      c.abort(c.enclosingPosition, s"routes file too many ${of.map(_.getAbsolutePath)}")
    else
      of.head
  }

  val appConfFile = {
    Play.confOutputDir.listFiles().filter(_.getName == "")
  }

  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val path = annotationParams.head.collect {
      case q"${Literal(Constant(path: String))}" ⇒ path
      case q"path=${Literal(Constant(path: String))}" ⇒ path
    }.head

    val clazz = c.typecheck(annottees.head.tree).symbol
    val clazzMembers = clazz.typeSignature.members
      .filter(e ⇒ e.annotations.nonEmpty)
      .map(e ⇒
        (e, e.annotations.filter(e ⇒
          e.tree.tpe <:< typeOf[Get] ||
            e.tree.tpe <:< typeOf[Post])
          ))
      .filter(_._2.nonEmpty)

    case class RouteLines(HttpMethod: String, url: String, codeMethod: String, params: String = "") {
      def id = HttpMethod + url + codeMethod
    }

    val codeRouteLines = clazzMembers.flatMap {
      case (method: c.universe.Symbol, annotations: List[c.universe.Annotation]) ⇒
        annotations.map(_.tree).map {
          case q"new ${annotation}(url = $url )" if annotation.tpe <:< typeOf[Get] ⇒
            val httpMethod = annotation.tpe match {
              case e if e <:< typeOf[Get] ⇒ "GET"
              case e if e <:< typeOf[Post] ⇒ "POST"
            }
            RouteLines(
              httpMethod,
              path + url.toString.tail.reverse.tail.reverse, method.fullName,
              if (method.asMethod.paramLists.isEmpty)
                ""
              else method.asMethod.paramLists.map(_.map(e => e.name.toString + ":" + e.info).mkString("(", ",", ")")).mkString
            )
        }
    }.toSeq

    val routes = scala.io.Source.fromFile(routesFile).getLines()
    val fileRoutesLines = {
      val asRequestUrl = "(.+) + ([a-z|A-Z|/|*]+) +([a-z|A-Z|\\\\.|0-9]+).*".r
      routes.collect {
        case asRequestUrl(a, b, c) ⇒ RouteLines(a.trim, b.trim, c.trim)
      }
    }
    val zero =
    //      fileRoutesLines.map(x ⇒ x.id  → x).toList.toMap
      fileRoutesLines
        .filterNot(e ⇒ codeRouteLines.exists(_.url == e.url))
        .filterNot(e ⇒ codeRouteLines.exists(_.codeMethod == e.codeMethod))
        .map(e ⇒ e.id → e).toMap
    val out = codeRouteLines
      .foldLeft(zero) { (l, r) ⇒
        l.+(r.id → r)
      }

    val hasChange =
      true
    //      !(
    //      out.size == zero.size &&
    //        zero.toList.sortBy(_._2.url).map(_._1).zip
    //        (out.toList.sortBy(_._2.url).map(_._1)).forall(e ⇒ e._1 == e._2)
    //      )

    if (hasChange) {
      val outRoutesFile = new PrintWriter(routesFile)

      val fileTxt =
        out.values.toList.sortBy(_.url).map(x ⇒ {
          s"${x.HttpMethod}  ${x.url}  ${x.codeMethod}${x.params}"
        }).mkString("\n")
      outRoutesFile.print(
        fileTxt
      )
      showInfo("routes file = \n" + show(fileTxt))
      outRoutesFile.close()
    }
    c.Expr(q"{..${annottees}}")
  }
}