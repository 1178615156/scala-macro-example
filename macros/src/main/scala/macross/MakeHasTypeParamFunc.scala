package macross

import scala.language.higherKinds
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by YuJieShui on 2015/10/27.
 */
private[macross] object MakeHasTypeParamFunc {
  def ol3[T]: Any = macro GetSealedSubClassImpl.ol3[T]

}

class MakeTpeParamFuncImpl(val c: Context) extends base.ShowInfo {

  import c.universe._

  def ol3[T: c.WeakTypeTag]: c.universe.Tree = {

    //get all sub class
    val subClass = c.weakTypeOf[T]
      .typeSymbol.asClass.knownDirectSubclasses
      .map(e => e.asClass.toType)

    //check type params must ia s sealed class
    if (subClass.size < 1)
      c.abort(c.enclosingPosition, s"${c.weakTypeOf[T]} is not a sealed class")

    // get sub class constructor params
    val subConstructorParams = subClass.map { e =>
      //get constructor
      e.members.filter(_.isConstructor)
        //if the class has many Constructor then you need filter the main Constructor
        .head.map(s => s.asMethod)
      //get function param list
    }.map(_.asMethod.paramLists.head)
      .map(_.map(e => q"""${e.name.toTermName}:${e.info} """))

    val outfunc = subClass zip subConstructorParams map {
      case (clas, parm) =>
        q"def smartConstructors[..${
          clas.typeArgs.map(_.toString).map(name => {
            //you must write like this
            //but why i also known
            TypeDef(Modifiers(Flag.PARAM), TypeName(name), List(), TypeBoundsTree(EmptyTree, EmptyTree))
          })
        }](..${parm})=${clas.typeSymbol.name.toTermName} (..${parm})"
    }
    val outClass =
      q"""
         object Term{
                 ..${outfunc}
                 }
          """
    showInfo(show(outClass))
    q"""{
      $outClass
        Term
      }

      """
  }
}