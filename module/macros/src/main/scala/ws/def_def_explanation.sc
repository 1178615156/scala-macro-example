import scala.language.experimental.macros

val universe: scala.reflect.runtime.universe.type =
  scala.reflect.runtime.universe

import universe._
//////////////////////////////////////////////////////
class Anno extends scala.annotation.Annotation

val defdef = q"""
            @Anno
           private def a[V] (i:Int,n:Option[Int]=None)(v:V)=i+1
""".asInstanceOf[DefDef]

defdef.mods //include private and @Anno
defdef.mods.hasFlag(Flag.PRIVATE) //true
defdef.mods.hasFlag(Flag.ABSTRACT) //false
defdef.mods.annotations //get annotations

defdef.name // a

defdef.tparams //param type [V]

defdef.vparamss // params value
defdef.vparamss.map(_.map(_.name))
defdef.vparamss.flatten.map(_.name) //get param name
defdef.vparamss.flatten.map(_.tpt) //get param type

//get name and type
defdef.vparamss.flatten.map(_.name) zip
  defdef.vparamss.flatten.map(_.tpt) toMap

//def defaults value
defdef.vparamss.flatten.map(_.rhs)