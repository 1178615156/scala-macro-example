package so

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yjs on 2015/12/2.
  */
class AnnotationWithTrait extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro AnnotationWithTraitImpl.apply

}

class AnnotationWithTraitImpl(val c: Context) {

  import c.universe._

  val SDKClasses = Set("java.lang.Object", "scala.Any")

  def showInfo(s: String) = c.info(c.enclosingPosition, s.split("\n").mkString("\n |---macro info---\n |", "\n |", ""), true)

  def apply(annottees: c.Expr[Any]*) = {
    val classDef = annottees.map(_.tree).head.asInstanceOf[ClassDef]
    val superClass = c.typecheck(classDef).symbol.asClass.baseClasses.tail
      .filterNot(e => SDKClasses.contains(e.fullName))

    showInfo(show(superClass.map(_.asClass.typeParams.map(_.name))))
    val superClassTree: List[Tree] = classDef match {
      case q"$mod class $name[..$t](..$params) extends ..$superClass { ..$body }" =>
        (superClass: List[Tree]).filterNot(e =>
          typeOf[Object].members.exists(_.name == e.children.head.toString())
        )
    }
    val impl = q"object ${TermName("impl")} extends ..${superClassTree}"
    //    val method = c.typecheck(classDef).symbol.asClass.info.members
    //      .filterNot(_.isConstructor)
    //      .filterNot(e => typeOf[Object].members.exists(_.name == e.name)).map(_.asMethod)
    //
    case class TypeParamsTran(from: String, to: String)
    val method = superClass.flatMap(_.info.members
      .filterNot(_.isConstructor)
      .filterNot(e => typeOf[Object].members.exists(_.name == e.name)).map(_.asMethod))

    val o = superClassTree zip superClass map {
      case (superClassTree, superClass) =>
          superClass.asClass.typeParams.map(_.name) zip superClassTree.children.tail map
            (e => TypeParamsTran(e._1.toString, e._2.toString()))


      //        val func: List[c.universe.MethodSymbol] = superClass.info.members
      //          .filterNot(_.isConstructor)
      //          .filterNot(e => typeOf[Object].members.exists(_.name == e.name)).map(_.asMethod).toList
      //        typeParams zip func
    }
    showInfo(show(o))

    //    map {
    //      case (typeParams: List[(c.universe.Tree, c.universe.Type)], func: List[c.universe.MethodSymbol]) =>
    //        func map { e =>
    //          val funcName = e.name
    //          val funcTypeParams = e.typeParams.map(_.name.toString).map(name => {
    //            TypeDef(Modifiers(Flag.PARAM), TypeName(name), List(), TypeBoundsTree(EmptyTree, EmptyTree))
    //          })
    //        }
    //
    //    }
    //    showInfo(show(impl))
    //    showInfo(show(method))
    val methodImpl =
      method map (e => {
        val funcName = e.name
        val funcTypeParams = e.typeParams.map(_.name.toString).map(name => {
          TypeDef(Modifiers(Flag.PARAM), TypeName(name), List(), TypeBoundsTree(EmptyTree, EmptyTree))
        })
        val funcParams = e.paramLists.map(_.map(e => q"${e.name.toTermName}:${e.info} "))
        q"""
           def ${funcName}[..${funcTypeParams}](...$funcParams):
        ${e.returnType}=
        impl.${funcName}[..${funcTypeParams}](...$funcParams)
            """
      }
        )
    showInfo(show(methodImpl))
    //    import Flag._
    //    DefDef(Modifiers(DEFERRED), TermName("f"), List(),
    //      List(List(ValDef(Modifiers(PARAM), TermName("a"), Ident(TypeName("Int")), EmptyTree), ValDef(Modifiers(PARAM), TermName("b"), Ident(
    //      TypeName("String")), EmptyTree)), List(ValDef(Modifiers(PARAM), TermName("c"), Ident(TypeName("Any")), EmptyTree))), Select(Ident("scala"), TypeName("Unit")), EmptyTree)
    //
    //    showInfo(showRaw(q"def f(a:Int,b:String)(c:Any)"))
    q"""
       class ${classDef.name}[..${classDef.tparams}]{
        $impl
..${methodImpl}
       }
      """
  }
}

//        ..${method.map(e=>q"""
//def ${e.name}[..${e.typeParams.map(_.name)}](...${e.paramLists.map(_.map(_.name))}) =
//impl.${e.name}[..${e.typeParams.map(_.name)}](...${e.paramLists.map(_.map(_.name))})
//
//                      """)}