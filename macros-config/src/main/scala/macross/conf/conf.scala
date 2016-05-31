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

  def as[T](implicit config: Config): T = ???

  def replace[T](f: Config => T)(implicit config: Config) = f(config)
}

class confImpl(val c: Context) {

  import c.universe._

  def loadConfig(annotation: List[Tree]): List[(Config, String)] = {
    val fileNames =
      annotation.filter(e => c.typecheck(e.duplicate).tpe <:< typeOf[ConfCheck]).map {
        case q"new $name(${Literal(Constant(file: String))})" => file
      }
    fileNames.map(fileName => ConfigFactory.load(this.getClass.getClassLoader, fileName) -> fileName)
  }

  def configExistCheck(config: Config, path: String, fileName: String,pos:Position) =
    if (!config.hasPath(path.toString))
      c.abort(pos, s"have not path:${path} in conf file:${fileName}")

  def asScalaBuffer(tree: Tree)={
    q"scala.collection.JavaConversions.asScalaBuffer($tree).toList"
  }
  def replaceConfigBase(path: TermName, needCheckConfig: List[(Config, String)]): PartialFunction[Tree, Tree] = {
    case v@q"conf.as[String](...$p)" =>
      q"conf.replace[String](_.getString(${Literal(Constant(path.toString))}))"

    case q"conf.as[Int](...$p)"      =>
      q"conf.replace[Int](_.getInt(${Literal(Constant(path.toString))}))"

    case q"conf.as[Boolean](...$p)"  =>
      q"conf.replace[Boolean](_.getBoolean(${Literal(Constant(path.toString))}))"

    case q"conf.as[Long](...$p)"  =>
      q"conf.replace[Long](_.getLong(${Literal(Constant(path.toString))}))"

    case q"conf.as[Long](...$p)"  =>
      q"conf.replace[Long](_.getLong(${Literal(Constant(path.toString))}))"

    case q"conf.as[Double](...$p)"  =>
      q"conf.replace[Double](_.getDouble(${Literal(Constant(path.toString))}))"

    case q"conf.as[Config](...$p)"  =>
      q"conf.replace[Config](_.getConfig(${Literal(Constant(path.toString))}))"



    case q"conf.as[List[Config]](...$p)"  =>
        q"conf.replace[List[Config]](e=>${asScalaBuffer(q"e.getList(${Literal(Constant(path.toString))})")})"

    case q"conf.as[List[Int]](...$p)"      =>
      q"conf.replace[List[Int]](e=>${asScalaBuffer(q"e.getIntList(${Literal(Constant(path.toString))})")}.map(_.toInt))"

    case q"conf.as[List[Boolean]](...$p)"  =>
        q"conf.replace[List[Boolean]](e=>${asScalaBuffer(q"e.getBooleanList(${Literal(Constant(path.toString))})")}.map(_.toBoolean))"

    case q"conf.as[List[Long]](...$p)"  =>
        q"conf.replace[List[Long]](e=>${asScalaBuffer(q"e.getLongList(${Literal(Constant(path.toString))})")}.map(_.toLong))"

    case q"conf.as[List[Double]](...$p)"  =>
        q"conf.replace[List[Double]](e=>${asScalaBuffer(q"e.getDoubleList(${Literal(Constant(path.toString))})")}.map(_.toDouble))"

    case v@q"conf.as[List[String]](...$p)" =>
        q"conf.replace[List[String]](e=>${asScalaBuffer(q"e.getStringList(${Literal(Constant(path.toString))})")}.map(_.toString))"

    case v@(q"conf.path" | q"path") =>
      Literal(Constant(path.toString))

  }

  def replaceConfPath2RealPath(tree: Tree, path: TermName, needCheckConfig: List[(Config, String)]): Tree = {
    def f = replaceConfigBase(path, needCheckConfig) andThen { e =>

      needCheckConfig.foreach { case (config, fileName) => configExistCheck(config, path.toString, fileName,tree.pos) }
      val log = needCheckConfig.map { case (config, fileName) => fileName + " :" + config.getValue(path.toString).toString }
      if (log.nonEmpty) c.info(tree.pos, log.mkString("\n[", ",", "]"), true)

      e
    } orElse[Tree, Tree] {

      case v@q"$a.$o" =>
        q"${replaceConfPath2RealPath(a, path, needCheckConfig)}.$o"

      case q"$a(...$p)" =>
        q"$a(...${p.map(_.map(e => replaceConfPath2RealPath(e, path, needCheckConfig)))})"

      case v@q"$a.$f(...$p)" =>
        q"${replaceConfPath2RealPath(a, path, needCheckConfig)}.$f(...${p.map(_.map(e => replaceConfPath2RealPath(e, path, needCheckConfig)))})"


      case e => e
    }

    f(tree)

  }


  def makeNewBody(oldBody: List[Tree], path: TermName, needCheckConfig: List[(Config, String)]) = {
    oldBody.map {
      case q"$mod def $$init$$(...$p) = {..$body}" =>
        q"def __init__(...$p) ={..$body}"

      case e: DefDef if e.name == termNames.CONSTRUCTOR => e

      case q"${mod} val $valueName:${valueType} ={..${body}}" =>
        val confName = TermName(path.toString + "." + valueName.toString())
        q"$mod val $valueName: $valueType = {..${body.map(e => replaceConfPath2RealPath(e, confName, needCheckConfig))}}"

      case v@q"${mod} def $valueName(...$p):${valueType} ={..${body}}" =>
        val confName = TermName(path.toString + "." + valueName.toString())
        q"$mod def $valueName(...$p):$valueType ={..${body.map(e => replaceConfPath2RealPath(e, confName, needCheckConfig))}}"

      case c: ClassDef  => replacePath(c, TermName(path + "." + c.name.toString), needCheckConfig)
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
      case c: ClassDef  => replacePath(c, c.name.toTermName, loadConfig(c.mods.annotations))
      case c: ModuleDef => replacePath(c, c.name.toTermName, loadConfig(c.mods.annotations))
      case e            => e
    }
    c.Expr(q"..${result}")
  }
}