package macros.annotation

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

/**
 * Created by YuJieShui on 2015/9/11.
 */
@compileTimeOnly("")
class MakeGetSet extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakeGetSetImpl.impl
}

class MakeGetSetImpl(val c: Context) {

  import c.universe._

  //get case class
  def getInClass(list_annottees: List[Tree]) = list_annottees match {
    case inClass :: Nil => inClass
  }

  //get case class params
  def getClassParams(c: Tree): List[ValDef] = {
    c match {
      //mod include
      //annotation
      //private[package]
      //case
      //other
      case q"$mod class $name(..$params) extends ..$bases { ..$body }" ⇒
        params
    }
  }

  //with function
  def classWithFunc(caseClass: Tree, listDef: List[DefDef]): c.universe.Tree = {
    caseClass match {
      case q"$mod class $name(..$params) extends ..$bases { ..$body }" ⇒
        q"""
            $mod class $name(..$params) extends ..$bases {
            ..$body
            ..$listDef
            }
            """
    }
  }

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    //get in case class
    val inCaseClass = getInClass(annottees.map(_.tree).toList)
    //get case class params
    val params = getClassParams(inCaseClass)
    //create get set method
    val getSetMethod =
      params.map((param: c.universe.ValDef) ⇒ {
        val mods = param.mods
        val name = param.name
        val tpt = param.tpt
        val anno = mods.annotations
        //get method name
        val get_name = TermName(
          s"get${name.toString.head.toString.toUpperCase + name.toString.tail}"
        )
        //set method name
        val set_name = TermName(
          s"set${name.toString.head.toString.toUpperCase + name.toString.tail}"
        )
        //make get method
        val getMethod =
          tpt.children.headOption match {

            /**
             *
             * if is a parameters type:
             * List[_],Option[_],Futrue[_] ...
             *
             * or high-end type
             * class C{
             * Type V=String
             * }
             */
            case Some(paramType) ⇒
              //is a Option[_]
              if (paramType.equalsStructure(tq"Option"))
                q"""@..${anno}
                def $get_name:${tpt.children(1)} = $name.getOrElse(null.asInstanceOf[${tpt.children(1)}])
                """
              else
              //other
                q"""@..${anno}
                    def $get_name=${name}
                    """
            case None ⇒
              q"""@..${anno}
                    def $get_name=${name}
                    """
          }
        //make set method
        val setMethod =
        //only is var then make set method
          if (mods.hasFlag(Flag.MUTABLE))
            tpt.children.headOption match {
              case Some(paramType) ⇒
                if (paramType.equalsStructure(tq"Option"))
                  q"""
                def $set_name(sss:${tpt.children(1)}):Unit=this.${name}=Option(sss)
                """
                else
                  q"""
                  def $set_name(sss:$tpt):Unit=this.$name=sss
                  """
              case None ⇒
                q"""
                  def $set_name(sss:$tpt):Unit=this.$name=sss
                  """
            }
          else
            q""

        List(getMethod, setMethod)
      })
    c.Expr(
      classWithFunc(inCaseClass, getSetMethod.flatten.asInstanceOf[List[DefDef]])
    )
  }

}
