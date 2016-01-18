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
//class MakePlayRoutesTest extends StaticAnnotation {
//  def macroTransform(annottees: Any*): Any = macro MakePlayRoutesTestImpl.annotationMakePlayRoutesImpl
//}
//class  MakePlayRoutesTestImpl(val c: blackbox.Context){
//  import c.universe._
//
//  def annotationMakePlayRoutesImpl(annottees: c.Expr[Any]*): c.Tree={
//    q"{..$annottees}"
//  }
//}

object MakePlayRoutes {
  def apply[T]: Unit = macro MakePlayRoutesImpl.objectApply[T]
}

object MakeUrlFile {
  def apply[T](filePath: String, packageName: String): Any = macro MakePlayRoutesImpl.mkUrlFile[T]
}

class MakePlayRoutesImpl(val c: blackbox.Context) extends spi.MakePlayRoutesMacroImpl {

  import c.universe._

  def objectApply[T: c.WeakTypeTag]: c.Tree = {
    val controller = c.weakTypeOf[T].typeSymbol
    val controllerPath = getControllerPath(controller)
    controllerPath.foreach(path ⇒ impl(controller, path))
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
//    Block(annottees.map(_.tree):_*)
  }

  def mkUrlFile[T: c.WeakTypeTag](filePath: c.Expr[String], packageName: c.Expr[String]) = {
    val controller = c.weakTypeOf[T].typeSymbol
    val controllerPath = controller.annotations.filter(_.tree.tpe <:< typeOf[Path]).map(_.tree).map {
      case q"new  ${annotation}(${Literal(Constant(path: String))} )" ⇒ path
      case q"new  ${annotation}(path= ${Literal(Constant(path: String))} )" ⇒ path
    }

    controllerPath.foreach(path ⇒ {
      val file = new File(c.eval(filePath).replaceAll("\\.", "\\") + "\\" + controller.name.toString + ".scala")
      if (!file.getParentFile.exists()) file.getParentFile.mkdirs()
      val a: Seq[RouteLine] = this.controllerRouteLines(controller, path)
      val printFile = new PrintWriter(file)

      val out: String = {
        a.map(_.url) zip a.map(_.url).map(_.replace(path, "")) map {
          case (url: String, name: String) ⇒
            s"""
               |val ${
              val h =
                if (name.head.toString == "/")
                  ""
                else
                  name.head.toString
              h + name.tail.replaceAll("/", "_")
            } = "$url"
           """.stripMargin
        } mkString "\n"
      }
      showInfo(show(out))
      printFile.write(
        s"""
           |package ${c.eval(packageName)}
           |object ${controller.name.toString}{
           |  $out
           |}
           |
         """.stripMargin)
      printFile.close()
    })


    q"()"
  }

  @deprecated
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

  @deprecated
  def pathImpl[T: c.WeakTypeTag](path: c.Expr[String]) = {
    val controller: c.universe.Symbol = c.weakTypeOf[T].typeSymbol
    impl(controller, c.eval(path))
    q"""
        ()
      """
  }

  @deprecated
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


@deprecated("user [[MakePlayRoutes]]")
class MakeRoutes(path: String) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakePlayRoutesImpl.annotationImpl
}


@deprecated("user [[MakePlayRoutes]]")
object MakeRoutes {

  def routesFilePath[T](path: String)(implicit routesFilePath: RoutesFilePath): Unit =
  macro MakePlayRoutesImpl.routesFilePathImpl[T]

  def apply[T](path: String): Unit =
  macro MakePlayRoutesImpl.pathImpl[T]
}
