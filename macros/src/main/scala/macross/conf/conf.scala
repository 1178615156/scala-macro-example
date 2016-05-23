package macross.conf

import scala.annotation.StaticAnnotation
import scala.reflect.macros.blackbox.Context
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

/**
  * Created by yujieshui on 2016/5/23.
  */
class conf extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro confImpl.impl
}

object conf {
  def auto_conf_path: String = ???
}

class confImpl(val c: Context) extends macross.base.ShowInfo {

  import c.universe._

  val auto_conf_path_name = "auto_conf_path"

  def replaceMethodPath(tree: Tree, real_path: TermName): Tree = {
    tree match {
      case q"$a.$f(..$p)" if a.toString() == auto_conf_path_name =>
        q"${Literal(Constant(real_path.toString))}.$f(..$p)"

      case q"$a.$f(..$p)" =>
        q"$a.$f(..${p.map(e => replaceMethodPath(e, real_path))})"

      case q"$t" if t.toString() == auto_conf_path_name =>
        Literal(Constant(real_path.toString))

      case e => e
    }
  }

  def replacePath(clazz: ClassDef, real_path: TermName): c.universe.ClassDef = {
    val newBody =
      clazz.impl.body.map {
        case q"$mod def $$init$$(...$p) = {..$body}" =>
          q"def __init__(...$p) ={..$body}"

        case v@q"${mod} val $valueName:${valueType} ={..${body}}" =>
          val confName = TermName(real_path.toString + "." + valueName.toString())
          q"$mod val $valueName: $valueType = {..${body.map(e => replaceMethodPath(e, confName))}}"

        case v@q"${mod} def $valueName(...$p):${valueType} ={..${body}}" =>
          val confName = TermName(real_path.toString + "." + valueName.toString())
          q"$mod def $valueName(...$p):$valueType ={..${body.map(e => replaceMethodPath(e, confName))}}"

        case c: ClassDef => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString))
        case c: ModuleDef => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString))

      }
    ClassDef(clazz.mods, clazz.name, clazz.tparams, Template(clazz.impl.parents, clazz.impl.self, newBody))
  }

  def replacePath(clazz: ModuleDef, real_path: TermName): ModuleDef = {
    val newBody =
      clazz.impl.body.map {
        case q"$mod def $$init$$(...$p) = {..$body}" =>
          q"def __init__(...$p) ={..$body}"

        case v@q"${mod} val $valueName:${valueType} ={..${body}}" =>
          val confName = TermName(real_path.toString + "." + valueName.toString())
          q"$mod val $valueName: $valueType = {..${body.map(e => replaceMethodPath(e, confName))}}"

        case v@q"${mod} def $valueName(...$p):${valueType} ={..${body}}" =>
          val confName = TermName(real_path.toString + "." + valueName.toString())
          q"$mod def $valueName(...$p):$valueType ={..${body.map(e => replaceMethodPath(e, confName))}}"

        case c: ClassDef => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString))
        case c: ModuleDef => replacePath(c, TermName(clazz.name.toString + "." + c.name.toString))

      }
    ModuleDef(clazz.mods, clazz.name, Template(clazz.impl.parents, clazz.impl.self, newBody))
  }

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {


    val result = annottees.map(_.tree).map {
      case c: ClassDef => replacePath(c, c.name.toTermName)
      case c: ModuleDef => replacePath(c, c.name.toTermName)
      case e => e
    }

    showInfo(show(
      result
    ))

    //    showInfo(show(clazz))
    c.Expr(q"..${result}")
  }
}