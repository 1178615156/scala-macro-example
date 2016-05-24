package macross.conf

import com.typesafe.config.{Config, ConfigFactory}

import scala.annotation.{Annotation, StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

/**
  * Created by yujieshui on 2016/5/23.
  */
class ConfCheck(val file: String) extends Annotation

class conf extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro confImpl.impl
}

object conf {
  def path: String = ???
}

class confImpl(val c: Context) extends macross.base.ShowInfo {

  import c.universe._

  val replacePathMethodName = List(
    "conf.path",
    "path"
  )

  def loadConfig(annotation: List[Tree]): List[(Config, String)] = {
    val fileNames =
      annotation.filter(e => c.typecheck(e.duplicate).tpe <:< typeOf[ConfCheck]).map {
        case q"new $name(${Literal(Constant(file: String))})" => file
      }
    fileNames.map(fileName => ConfigFactory.load(this.getClass.getClassLoader, fileName) -> fileName)
  }

  def replaceMethodPath(tree: Tree, path: TermName, needCheckConfig: List[(Config, String)]): Tree = {
    tree match {
      case v@q"$a.$f(..$p)" if replacePathMethodName.contains(a.toString()) =>
        needCheckConfig.foreach { case (config, fileName) => configExistCheck(config, path.toString, fileName) }
        val log = needCheckConfig.map { case (config, fileName) => fileName + " :" + config.getValue(path.toString).toString }
        if (log.nonEmpty)
          c.info(v.pos, log.mkString("\n[", ",", "]"), true)


        q"${Literal(Constant(path.toString))}.$f(..$p)"

      case q"$a.$f(..$p)" =>
        q"$a.$f(..${p.map(e => replaceMethodPath(e, path, needCheckConfig))})"

      case v@q"$t" if replacePathMethodName.contains(t.toString()) =>
        needCheckConfig.foreach { case (config, fileName) => configExistCheck(config, path.toString, fileName) }
        val log = needCheckConfig.map { case (config, fileName) => fileName + " :" + config.getValue(path.toString).toString }
        if (log.nonEmpty)
          c.info(v.pos, log.mkString("\n[", ",", "]"), true)

        Literal(Constant(path.toString))

      case e => e
    }
  }

  def configExistCheck(config: Config, path: String, fileName: String) =
    if (!config.hasPath(path.toString))
      c.error(c.enclosingPosition, s"have not path:${path} in conf file:${fileName}")


  def makeNewBody(oldBody: List[Tree], path: TermName, needCheckConfig: List[(Config, String)]) = {
    oldBody.map {
      case q"$mod def $$init$$(...$p) = {..$body}" =>
        q"def __init__(...$p) ={..$body}"

      case e: DefDef if e.name == termNames.CONSTRUCTOR => e

      case v@q"${mod} val $valueName:${valueType} ={..${body}}" =>
        val confName = TermName(path.toString + "." + valueName.toString())
        q"$mod val $valueName: $valueType = {..${body.map(e => replaceMethodPath(e, confName,needCheckConfig))}}"

      case v@q"${mod} def $valueName(...$p):${valueType} ={..${body}}" =>
        val confName = TermName(path.toString + "." + valueName.toString())
        q"$mod def $valueName(...$p):$valueType ={..${body.map(e => replaceMethodPath(e, confName,needCheckConfig))}}"

      case c: ClassDef => replacePath(c, TermName(path + "." + c.name.toString), needCheckConfig)
      case c: ModuleDef => replacePath(c, TermName(path + "." + c.name.toString), needCheckConfig)

    }
  }

  def replacePath(clazz: ModuleDef, path: TermName, needCheckConfig: List[(Config, String)]): ModuleDef = {
    val newBody = makeNewBody(clazz.impl.body, path, needCheckConfig)
    ModuleDef(clazz.mods, clazz.name, Template(clazz.impl.parents, clazz.impl.self, newBody))
  }

  def replacePath(clazz: ClassDef, path: TermName, needCheckConfig: List[(Config, String)]): c.universe.ClassDef = {
    val newBody = makeNewBody(clazz.impl.body, path, needCheckConfig)
    ClassDef(clazz.mods, clazz.name, clazz.tparams, Template(clazz.impl.parents, clazz.impl.self, newBody))
  }

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    val result = annottees.map(_.tree).map {
      case c: ClassDef => replacePath(c, c.name.toTermName, loadConfig(c.mods.annotations))
      case c: ModuleDef => replacePath(c, c.name.toTermName, loadConfig(c.mods.annotations))
      case e => e
    }

    c.Expr(q"..${result}")
  }
}