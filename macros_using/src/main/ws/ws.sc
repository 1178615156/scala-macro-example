import scala.language.experimental.macros;
val universe: scala.reflect.runtime.universe.type =
  scala.reflect.runtime.universe

import universe._

val a = q"val a:Int =1"

object Module_1 {
  val a = 1
  val b = 2
  val c = 3
  val l = List(1, 2)
  //need write return type
}
q"Int"

val t=weakTypeOf[Module_1.type].members
  .filter(_.isMethod)
  .map(_.asMethod)
  .filter(_.isGetter)
  .map(_.info.resultType)
t.map(e=>e.=:=(typeOf[scala.Int]))
//  .map(_.baseClasses)
//.map(_.map(_.asClass))
a match {
  case ValDef(a, b, c, d) => c
}