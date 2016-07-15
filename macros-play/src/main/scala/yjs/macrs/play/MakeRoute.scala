package yjs.macrs.play


import java.io.File

import com.typesafe.config.ConfigFactory

import scala.meta._

/**
  * Created by yuJieShui on 2016/7/15.
  */
//todo wait impl
object MakeRoute {

  case class RouteLine(httpMethod: String, url: String, codeMethod: String, params: String = "") {
    def id = httpMethod + url + codeMethod
  }

  def userDir = new File(ConfigFactory.load().getString("user.dir")).getAbsolutePath

  def routesFile: File = new File(s"${userDir}/conf/routes")

  def controllerRouteLines(stats: Seq[Stat]) = {
    stats.collect {
      case x: Defn.Def =>
        println(x.mods)
        x.mods.collect{case Mod.Annot(annot) => annot}
    }
  }

  def impl(defn: Any) = defn match {
    case x: Defn.Trait  => controllerRouteLines(x.templ.stats.getOrElse(Nil))
    case x: Defn.Class  => controllerRouteLines(x.templ.stats.getOrElse(Nil))
    case x: Defn.Object => controllerRouteLines(x.templ.stats.getOrElse(Nil))
  }
}

import MakeRoute._

final class MakeRoute extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any) = meta {
//    val out = impl(defn)
    println(routesFile)
    println(impl(defn))
    println(defn)
    defn
  }
}
