package yjs.macrs.conf

import com.typesafe.config.Config

import scala.meta.Defn._
import scala.meta._

/**
  * Created by yuJieShui on 2016/7/13.
  */

object conf {

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
      q"${replace_method_body(a,path)}(..${p.map(e => replace_method_body(e, path))})"
    case q"$a.$f(..$p)" =>
      q"$a.$f(..${p.map(e => replace_method_body(e, path))})"
    case other: Term    =>
      other
  }

  def replace_class(any: Any, path: Option[String]): Stat = any match {
    case x: Def         => x.copy(body = replace_method_body(x.body, path.map(_ +".").getOrElse("") + x.name))
    case x: Val         => x.copy(rhs = replace_method_body(x.rhs, path.map(_ +".").getOrElse("")  + x.pats.head))
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