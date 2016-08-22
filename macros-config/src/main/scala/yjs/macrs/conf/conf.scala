package yjs.macrs.conf

import com.typesafe.config.Config

import scala.language.experimental.macros


/**
  * Created by yuJieShui on 2016/7/13.
  */

class confApplyImpl(val c: scala.reflect.macros.blackbox.Context)
  extends yjs.macrs.common.ProjectProperty {

  import c.universe._

  def packageName(x: Symbol): String =
    if (x.owner.isPackage) x.fullName.toString else packageName(x.owner)

  def impl[T](config: c.Expr[Config])(implicit t: c.WeakTypeTag[T]): c.Tree = {

    val configName = ownerName.replace(packageName(c.internal.enclosingOwner), "").tail

    def asScala(tree: Tree): Tree =
      q"scala.collection.JavaConversions.asScalaBuffer($tree).toList"

    val out = c.weakTypeOf[T] match {
      case x if x <:< typeOf[Int]     => q"$config.getInt($configName)"
      case x if x <:< typeOf[Boolean] => q"$config.getBoolean($configName)"
      case x if x <:< typeOf[Double]  => q"$config.getDouble($configName)"
      case x if x <:< typeOf[String]  => q"$config.getString($configName)"
      case x if x <:< typeOf[Config]  => q"$config.getConfig($configName)"
      case x if x <:< typeOf[Long]    => q"$config.getLong($configName)"

      case x if x <:< typeOf[List[Int]]     => q"${asScala(q"$config.getIntList($configName)")}.map(_.toInt)"
      case x if x <:< typeOf[List[Boolean]] => q"${asScala(q"$config.getBooleanList($configName)")}.map(_.toBoolean)"
      case x if x <:< typeOf[List[Double]]  => q"${asScala(q"$config.getDoubleList($configName)")}.map(_.toDouble)"
      case x if x <:< typeOf[List[String]]  => q"${asScala(q"$config.getStringList($configName)")}"
      case x if x <:< typeOf[List[Config]]  => q"${asScala(q"$config.getConfigList($configName)")}"
      case x if x <:< typeOf[List[Long]]    => q"${asScala(q"$config.getLongList($configName)")}.map(_.toLong)"
    }
    c.echo(c.internal.enclosingOwner.pos,"\n" +  show(configName))
    out
  }
}

object conf {

  def apply[T](implicit config: Config): T = macro confApplyImpl.impl[T]

  import scala.meta.Defn._
  import scala.meta._

  @deprecated("user conf[Type]","")
  def as[T](implicit config: Config): T = ???

  def replace[T](f: Config => T)(implicit config: Config) = f(config)

  def asScalaBuffer(tree: Term): Term =
    q"scala.collection.JavaConversions.asScalaBuffer($tree).toList"

  def replace_method_body(body: Tree, path: String): Term = body match {
    case q"conf.as[Int]"     => q"conf.replace[Int](_.getInt($path))"
    case q"conf.as[String]"  => q"conf.replace[String](_.getString($path))"
    case q"conf.as[Boolean]" => q"conf.replace[Boolean](_.getBoolean($path))"
    case q"conf.as[Long]"    => q"conf.replace[Long](_.getLong($path))"
    case q"conf.as[Double]"  => q"conf.replace[Double](_.getDouble($path))"
    case q"conf.as[Config]"  => q"conf.replace[Config](_.getConfig($path))"

    case q"conf.as[List[Int]]"     => q"conf.replace[List[Int]](e=>${asScalaBuffer(q"e.getIntList($path)")}.map(_.toInt))"
    case q"conf.as[List[String]]"  => q"conf.replace[List[String]](e=>${asScalaBuffer(q"e.getStringList($path)")}.map(_.toString))"
    case q"conf.as[List[Boolean]]" => q"conf.replace[List[Boolean]](e=>${asScalaBuffer(q"e.getBoolList($path)")}.map(_.toBoolean))"
    case q"conf.as[List[Long]]"    => q"conf.replace[List[Long]](e=>${asScalaBuffer(q"e.getLongList($path)")}.map(_.toLong))"
    case q"conf.as[List[Double]]"  => q"conf.replace[List[Double]](e=>${asScalaBuffer(q"e.getDoubleList($path)")}.map(_.toDouble))"
    case q"conf.as[List[Config]]"  => q"conf.replace[List[Config]](e=>${asScalaBuffer(q"e.getConfigList($path)")})"

    case q"$a.$b"       =>
      q"${replace_method_body(a, path)}.$b"
    case q"$a(..$p)"    =>
      q"${replace_method_body(a, path)}(..${p.map(e => replace_method_body(e, path))})"
    case q"$a.$f(..$p)" =>
      q"$a.$f(..${p.map(e => replace_method_body(e, path))})"
    case other: Term    =>
      other
  }

  def replace_class(any: Any, path: Option[String]): Stat = any match {
    case x: Def         => x.copy(body = replace_method_body(x.body, path.map(_ + ".").getOrElse("") + x.name))
    case x: Val         => x.copy(rhs = replace_method_body(x.rhs, path.map(_ + ".").getOrElse("") + x.pats.head))
    case x: Defn.Trait  => x.copy(templ = x.templ.copy(stats = x.templ.stats.map(_.map(stat => replace_class(stat, Some(path.map(_ + "." + x.name.toString()).getOrElse(x.name.toString())))))))
    case x: Defn.Class  => x.copy(templ = x.templ.copy(stats = x.templ.stats.map(_.map(stat => replace_class(stat, Some(path.map(_ + "." + x.name.toString()).getOrElse(x.name.toString())))))))
    case x: Defn.Object => x.copy(templ = x.templ.copy(stats = x.templ.stats.map(_.map(stat => replace_class(stat, Some(path.map(_ + "." + x.name.toString()).getOrElse(x.name.toString())))))))
    case other: Stat    => other
  }

}

import conf._

final class conf extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any) = meta {
    val out = replace_class(defn, None)
    out
  }
}