package macross.slick

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import slick.lifted.{Rep, ForeignKeyQuery}


/**
  * Created by yu jie shui on 2015/11/26 16:21.
  */
object SlickStarMacro {
  def apply[ClassType, ModelType]: slick.lifted.ProvenShape[ModelType] = macro SlickStarMacroImpl.apply[ClassType, ModelType]
}

class SlickStarMacroImpl(val c: Context)
  extends macross.base.ShowInfo
  with spi.GetTableRepValue {

  import c.universe._

  def apply[ClassType: c.WeakTypeTag, ModelType: c.WeakTypeTag]: c.universe.Tree = {
    val classType = c.weakTypeOf[ClassType]
    val modelType = c.weakTypeOf[ModelType]
    val reps =
      tableRepValue[ClassType]
        .toList.reverse.map(_.name.toTermName)

    //    showInfo(show(reps))
    val repsHList = reps.foldRight(c.Expr(q"HNil")) { (l, r) ⇒
      c.Expr(q"new HCons($l,$r)")
    }
    //    showInfo(show(repsHList))
    val hListToModel = {
      q"{case hlist => ${modelType.typeSymbol.name.toString: TermName}(..${
        reps.indices zip reps map {
          case (index, rep) ⇒ q"$rep = hlist($index)"
        }
      })}"
    }
    val modelToHList = {
      q"(model:$modelType) => Option((${
        reps.map(x ⇒ q"model.$x").foldRight(c.Expr(q"HNil")) { (l, r) ⇒
          c.Expr(q"new HCons($l,$r)")
        }
      }))"
    }
    //    showInfo(show(hListToModel))
    //    showInfo(show(modelToHList))
    val rt =
      q"""{
   import slick.collection.heterogeneous._
    ($repsHList).shaped<>($hListToModel,$modelToHList)
        }
      """

    showInfo("def * =" + show(rt))
    rt
  }
}
