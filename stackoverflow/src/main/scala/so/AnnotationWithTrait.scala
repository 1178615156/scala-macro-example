    package so

    import scala.annotation.StaticAnnotation
    import scala.language.experimental.macros
    import scala.reflect.macros.blackbox.Context

    /**
      * Created by yu jie shui on 2015/12/2.
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

        val superClassSymbol= c.typecheck(classDef).symbol.asClass.baseClasses.tail
          .filterNot(e => SDKClasses.contains(e.fullName)).reverse

        val superClassTree= classDef match {
          case q"$mod class $name[..$t](..$params) extends ..$superClass { ..$body }" =>
            (superClass: List[Tree]).filterNot(e =>
              typeOf[Object].members.exists(_.name == e.children.head.toString())
            )
        }

        showInfo(show(superClassSymbol))
        showInfo(show(superClassTree))

        val impl = q"private[this] object ${TermName("impl")} extends ..${superClassTree}"
        //

        //get super class all can call method
        val methods = superClassSymbol.map(_.info.members
          .filterNot(_.isConstructor)
          .filterNot(e => typeOf[Object].members.exists(_.name == e.name)).map(_.asMethod)).toList

        case class ReplaceTypeParams(from: String, to: String)
        type ClassReplace = List[ReplaceTypeParams]

        //trait a[A]
        //class b[B] extends a[B]
        //need replace type params A to B
        val classReplaceList: List[ClassReplace] = superClassTree zip superClassSymbol map {
          case (superClassTree, superClassSymbol) =>
            superClassSymbol.asClass.typeParams.map(_.name) zip superClassTree.children.tail map
              (e => ReplaceTypeParams(e._1.toString, e._2.toString()))
        }

        val out = classReplaceList zip methods map {
          case (classReplace, func) =>

            func map { e => {

              val funcName = e.name

              val funcTypeParams = e.typeParams.map(_.name.toString).map(name => {
                TypeDef(Modifiers(Flag.PARAM), TypeName(name), List(), TypeBoundsTree(EmptyTree, EmptyTree))
              })

              val funcParams = e.paramLists.map(_.map(e => q"${e.name.toTermName}:${
                TypeName(
                  classReplace.find(_.from == e.info.toString).map(_.to).getOrElse(e.info.toString)
                )} "))

              val funcResultType = TypeName(
                classReplace.find(_.from == e.returnType.toString).map(_.to).getOrElse(e.info.toString)
              )
              q"""
               def ${funcName}[..${funcTypeParams}](...$funcParams):${funcResultType}=
                  impl.${funcName}[..${funcTypeParams}](...$funcParams)
                """
            }
            }

        }

        showInfo(show(out))

        q"""
           class ${classDef.name}[..${classDef.tparams}]{
            $impl
            ..${out.flatten}
           }
          """
      }
    }

