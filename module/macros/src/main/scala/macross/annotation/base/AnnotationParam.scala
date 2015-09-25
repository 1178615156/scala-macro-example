package macross.annotation.base

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/24.
 */
trait AnnotationParam {
  val c: Context

  import c.universe._

  def annotationParam(name: Name): Tree =
    c.macroApplication match {
      case q"new $annotationName (..$param).$fn(..$bn)" => param.collect {
        case q"$name=$v" => v
      }.head
    }
}
