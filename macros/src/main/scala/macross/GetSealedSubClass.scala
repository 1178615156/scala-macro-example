package macross

import scala.language.higherKinds
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/10/16.
 */
//class GetSealedSubClass {
//
//}
object GetSealedSubClass {
  def ol[T]: List[T] = macro GetSealedSubClassImpl.apply[T]

  def ol2[T]: Any = macro GetSealedSubClassImpl.ol2[T]


  def ol3[T]: Any = macro GetSealedSubClassImpl.ol3[T]
}

class GetSealedSubClassImpl(val c: Context) extends base.ShowInfo {

  import c.universe._

  def apply[T: c.WeakTypeTag] = {
    val sub = c.weakTypeOf[T].typeSymbol.asClass.knownDirectSubclasses
    //    showInfo(showRaw(sub))
    val rt = q"List(..${sub.map(e ⇒ q"${e.asClass.module}")})"
    showInfo(showRaw(rt))
    rt
  }

  def ol2[T: c.WeakTypeTag] = {
    val sub = c.weakTypeOf[T].typeSymbol.asClass.knownDirectSubclasses
    showInfo(show(sub.map(_.asClass.filter(_.isConstructor))))
    q""
  }

  def ol3[T: c.WeakTypeTag] = {

    val sub = c.weakTypeOf[T]
      .typeConstructor.typeSymbol.asClass.knownDirectSubclasses
      .map(e => e.asClass.toType)
    val subParm = sub.map { e =>
      e.members.filter(_.isConstructor).head.map(s => s.asMethod)
    }.map(_.asMethod.paramLists.head).map(_.map(e => q"""${e.name.toTermName}:${e.info} """))

    val outfunc =
      sub zip subParm map {
        case (clas, parm) =>
          val a = clas.typeArgs.map(_.toString: TermName).map(e => q"$e")
          q"""
            def smartConstructors[..${a}](..${parm})=${clas.typeSymbol.fullName: TermName} (..${parm})
            """
      }
    showInfo(show(outfunc))
    q"""
      {
      class Term{
        def smartConstructors=1
        }
       new Term
      }

      """
  }
}