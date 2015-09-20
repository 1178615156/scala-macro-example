package macros.annotation.base

import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/11.
 */
trait GetInClass {
  val c: Context

  import c.universe._

  //get case class
  def getInClass(list_annottees: List[Tree]) = list_annottees.collect {
    case cc: ClassDef => cc
  }.head
}
