package macross.play

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

import java.io.{PrintWriter, File}

import macross.play.spi.RouteLine
import yjs.annotation.Routes.Path

/**
  * Created by yu jie shui on 2015/12/11 15:25.
  */

/**
  * @example :{{{
                  @Path("/controller")
                  @MakePlayRoutes
                  class Controller {
                    @Get( url = "/hello")
                    def hello = ???

                  }
  *          }}}
  *
  */
class MakePlayRoutes extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakePlayRoutesImpl.annotationMakePlayRoutesImpl
}

object MakePlayRoutes {
  def apply[T]: Unit = macro MakePlayRoutesImpl.objectApply[T]

  def from[T](route_file_path: String): Unit = macro MakePlayRoutesImpl.from[T]


}

class MakePlayRoutesImpl(val c: blackbox.Context)
  extends spi.MakePlayRoutesMacroImpl {

  import c.universe._

  def objectApply[T: c.WeakTypeTag]: c.Tree = {
    val controller = c.weakTypeOf[T].typeSymbol
    val controllerPath = getControllerPath(controller)
    controllerPath.foreach(path ⇒ impl(controller, path))
    q"()"
  }

  def from[T: c.WeakTypeTag](route_file_path: c.Expr[String]): c.Tree = {
    val controller = c.weakTypeOf[T].typeSymbol
    val controllerPath = getControllerPath(controller)
    controllerPath.foreach(path ⇒ impl(controller, path, Some(new File(rootProjectDir + "/" + c.eval(route_file_path)))))
    q"()"
  }

  def getControllerPath(controller: Symbol): List[String] = {
    controller.annotations.filter(_.tree.tpe <:< typeOf[Path]).map(_.tree).map {
      case q"new  ${annotation}(${Literal(Constant(path: String))} )" ⇒ path
      case q"new  ${annotation}(path= ${Literal(Constant(path: String))} )" ⇒ path
    }
  }

  def annotationMakePlayRoutesImpl(annottees: c.Expr[Any]*): c.Tree = {
    showInfo(show("--in----"))
    val controller: c.universe.Symbol = c.typecheck(annottees.head.tree).symbol
    val controllerPath = getControllerPath(controller)
    controllerPath.foreach(path ⇒ impl(controller, path))

    showInfo(show((q"{..${annottees}}")))
    q"{..$annottees}"
  }
}
