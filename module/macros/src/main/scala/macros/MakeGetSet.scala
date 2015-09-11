package macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/9/4.
 */
@compileTimeOnly("")
class MakeGetSet extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro AnnotationGetSetMacroImpl.impl
}

class AnnotationGetSetMacroImpl(val c: Context) {

  import c.universe._

  //get case class
  def getInCaseClass(list_annottees: List[Tree]) = list_annottees match {
    case caseClass :: Nil => caseClass
    case caseClass :: companionObject :: Nil => caseClass
  }

  def impl(annottees: c.Expr[Any]*): c.Expr[Any] = {
    val inCaseClass = getInCaseClass(annottees.map(_.tree).toList)

    val out: c.universe.Tree = inCaseClass match {
      //if inCaseClass no a case class
      //throw match error
      case q"case class $name(..$params) extends ..$bases { ..$body }" =>

        val list_params: List[ValDef] = params.asInstanceOf[List[ValDef]]

        val get_set_params_func = list_params.map((param: c.universe.ValDef) => {
          //get method name
          val get_name = TermName("get" + {
            val vn = param.name.toString;
            vn.head.toString.toUpperCase() + vn.tail
          })
          //set method name
          val set_name = TermName("set" + {
            val vn = param.name.toString;
            vn.head.toString.toUpperCase() + vn.tail
          })

          param match {
            case ValDef(mods: Modifiers, name: TermName, tpt: Tree, rhs: Tree) =>
              val v_type: c.universe.Tree = tq"$tpt"

              //如果参数类型为泛型参数
              v_type.children.headOption.map {
                // where type is Option[_]
                case tq"Option" =>
                  Tuple2(
                    q"""@..${mods.annotations}
                        def $get_name = $name.orNull
                        """,
                    //where value is var
                    //make set method
                    if (mods.hasFlag(Flag.MUTABLE))
                      q"def $set_name(sss:${v_type.children(1)}):Unit=this.${name}=Option(sss)"
                    else
                      q""
                  )
                //other such : Future[_],List[_]
                case _ =>
                  Tuple2(
                    q"def $get_name = ${param.name}",
                    q""
                  )
              } //非泛型参数
                .getOrElse(Tuple2(
                q"def $get_name= {${param.name}}",
                if (mods.hasFlag(Flag.MUTABLE))
                  q"def $set_name(sss:$v_type):Unit=this.${name}=sss"
                else
                  q""
              ))
          }
        }).flatMap(t2 => List(t2._1, t2._2))

        q"""
            case class $name(..$params) extends ..$bases {
            ..$get_set_params_func
            ..$body
            }
            """
    }
    c.Expr(out)
  }
}