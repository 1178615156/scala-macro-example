package macross.conf

import com.typesafe.config.{Config, ConfigFactory}

import scala.annotation.{Annotation, StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

/**
  * Created by yujieshui on 2016/5/23.
  */
class conf_check(val file: String) extends Annotation

class conf extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro confImpl.impl
}

object conf {
  def auto_conf_path: String = ???
}

class confImpl(val c: Context) extends macross.base.ShowInfo {

  import c.universe._

  val auto_conf_path_name = "auto_conf_path"

  def replaceMethodPath(tree: Tree, real_path: TermName, checkConfig: List[(Config, String)] = Nil): Tree = {
    tree match {
      case q"$a.$f(..$p)" if a.toString() == auto_conf_path_name =>
        q"${Literal(Constant(real_path.toString))}.$f(..$p)"

      case q"$a.$f(..$p)" =>
        q"$a.$f(..${p.map(e => replaceMethodPath(e, real_path, checkConfig))})"

      case q"$t" if t.toString() == auto_conf_path_name =>
        Literal(Constant(real_path.toString))

      case e => e
    }
  }

  def configExistCheck(config: Config, path: String, fileName: String) = {
    if (!config.hasPath(path.toString))
      c.error(c.enclosingPosition, s"have not path:${path} in conf file:${fileName}")
  }

  //todo merge to public impl
  def replacePath(clazz: ClassDef, namePath: TermName, checkConfig: List[(Config, String)]): c.universe.ClassDef = {
    val newBody =
      clazz.impl.body.map {
        case q"$mod def $$init$$(...$p) = {..$body}" =>
          q"def __init__(...$p) ={..$body}"

        case q"${mod} val $valueName:${valueType} ={..${body}}" =>
          val confName = TermName(namePath.toString + "." + valueName.toString())
          checkConfig.foreach { case (config, fileName) => configExistCheck(config, confName.toString, fileName) }
          q"$mod val $valueName: $valueType = {..${body.map(e => replaceMethodPath(e, confName))}}"

        case q"${mod} def $valueName(...$p):${valueType} ={..${body}}" =>
          val confName = TermName(namePath.toString + "." + valueName.toString())
          checkConfig.foreach { case (config, fileName) => configExistCheck(config, confName.toString, fileName) }
          q"$mod def $valueName(...$p):$valueType ={..${body.map(e => replaceMethodPath(e, confName))}}"

        case c: ClassDef  => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString), checkConfig)
        case c: ModuleDef => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString), checkConfig)

      }
    ClassDef(clazz.mods, clazz.name, clazz.tparams, Template(clazz.impl.parents, clazz.impl.self, newBody))
  }

  def replacePath(clazz: ModuleDef, namePath: TermName, checkConfig: List[(Config, String)]): ModuleDef = {
    val newBody =
      clazz.impl.body.map {

        case e:DefDef if e.name == termNames.CONSTRUCTOR              => e
        case q"${mod} val $valueName:${valueType} ={..${body}}" =>
          val confName = TermName(namePath.toString + "." + valueName.toString())
          checkConfig.foreach { case (config, fileName) => configExistCheck(config, confName.toString, fileName) }
          q"$mod val $valueName: $valueType = {..${body.map(e => replaceMethodPath(e, confName))}}"

        case q"${mod} def $valueName(...$p):${valueType} ={..${body}}" =>
          val confName = TermName(namePath.toString + "." + valueName.toString())
          checkConfig.foreach { case (config, fileName) => configExistCheck(config, confName.toString, fileName) }
          q"$mod def $valueName(...$p):$valueType ={..${body.map(e => replaceMethodPath(e, confName))}}"

        case c: ClassDef  => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString), checkConfig)
        case c: ModuleDef => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString), checkConfig)

      }
    ModuleDef(clazz.mods, clazz.name, Template(clazz.impl.parents, clazz.impl.self, newBody))
  }

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {


    def loadConfig(annotation: List[Tree]) = {
      val fileNames =
        annotation.filter(e => this.c.typecheck(e.duplicate).tpe <:< typeOf[conf_check]).map {
          case q"new $name(${Literal(Constant(file: String))})" => file
        }
      fileNames.map(fileName => ConfigFactory.load(this.getClass.getClassLoader, fileName) -> fileName)
    }
    val result = annottees.map(_.tree).map {
      case c: ClassDef  =>
        replacePath(c, c.name.toTermName, loadConfig(c.mods.annotations))
      case c: ModuleDef =>
        replacePath(c, c.name.toTermName, loadConfig(c.mods.annotations))
      case e            => e
    }

    showInfo(show(
      result
    ))

    //    showInfo(show(clazz))
    c.Expr(q"..${result}")
  }
}