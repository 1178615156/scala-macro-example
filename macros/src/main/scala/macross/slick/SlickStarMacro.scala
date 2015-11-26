package macross.slick

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import slick.lifted.{Rep, ForeignKeyQuery}

private case class SlickModelColumnSwap[Column, ModelField]
(rep: Rep[Column])
(c2m: Column ⇒ ModelField, m2c: ModelField ⇒ Column)

/**
  * Created by yu jie shui on 2015/11/26 16:21.
  */
object SlickStarMacro {
  def apply[ClassType, ModelType]: slick.lifted.ProvenShape[ModelType] =
  macro SlickStarMacroImpl.apply[ClassType, ModelType]

  //todo
  //  private def apply_v2[ClassType, ModelType](fs: SlickModelColumnSwap[_, _]*): slick.lifted.ProvenShape[ModelType] =
  //  macro SlickStarMacroImpl.apply[ClassType, ModelType]
}

class SlickStarMacroImpl(val c: Context) extends macross.base.ShowInfo {

  import c.universe._

  //todo
  //  def apply_v2[ClassType: c.WeakTypeTag, ModelType: c.WeakTypeTag](fs: c.Expr[SlickModelColumnSwap[_, _]]*): c.universe.Tree

  def apply[ClassType: c.WeakTypeTag, ModelType: c.WeakTypeTag]: c.universe.Tree = {
    val classType = c.weakTypeOf[ClassType]
    val modelType = c.weakTypeOf[ModelType]
    val reps = classType.members
      .filter(_.isMethod).map(_.asMethod)
      .filter(_.isPublic)
      .filter(_.info.resultType <:< typeOf[slick.lifted.Rep[_]])
      .filter(e ⇒ !(e.info.resultType <:< typeOf[ForeignKeyQuery[_, _]]))
      //      .filter(e ⇒ modelType.members.map(_.name.toString).exists(_ == e.name.toString))
      .toList.reverse.map(_.name.toTermName)

    showInfo(show(reps))
    val repsHList = reps.foldRight(c.Expr(q"HNil")) { (l, r) ⇒
      c.Expr(q"new HCons($l,$r)")
    }
    showInfo(show(repsHList))
    def hListToModel = {
      q"{case hlist => ${modelType.companion.typeSymbol.name.toString: TermName}(..${
        reps.indices zip reps map {
          case (index, rep) ⇒ q"hlist($index)"
        }
      })}"
    }
    def modelToHList = {
      q"(model:$modelType) => Option((${
        reps.map(x ⇒ q"model.$x").foldRight(c.Expr(q"HNil")) { (l, r) ⇒
          c.Expr(q"new HCons($l,$r)")
        }
      }))"
    }
    showInfo(show(hListToModel))
    showInfo(show(modelToHList))
    val rt =
      q"""{
   import slick.collection.heterogeneous._
    (${repsHList}).shaped<>($hListToModel,$modelToHList)
        }
      """

    showInfo(show(rt))
    rt
  }
}
