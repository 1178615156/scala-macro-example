package macross.tran

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.macros.blackbox

/**
  * Created by yujieshui on 2016/3/11.
  */


trait TranMethod[In, To] {
}

trait TranConfig

object TranConfig extends TranConfig {

  trait Default extends TranConfig {
    final val option_option_2_option    = new TranMethod[Option[Option[_]], Option[_]] {
      def apply[T](in: Option[Option[T]]): Option[T] = in.flatten
    }
    final val list_future_2_future_list = new TranMethod[List[Future[_]], Future[List[_]]] {
      def apply[T](in: List[Future[T]])(implicit executor: ExecutionContext): Future[List[T]] = Future.sequence(in)
    }
  }

  implicit final object Default extends Default

}

object Tran2 {
  def apply[In, To](in: In)(implicit tranConfig: TranConfig): Any = macro Tran2Impl.applyTranTo[In, To]
}

class Tran2Impl(val c: blackbox.Context) extends macross.base.ShowInfo {

  import scala.concurrent.Future
  import scala.language.experimental.macros
  import scala.reflect.macros.blackbox
  import scala.reflect.macros.blackbox.Context
  import scala.reflect.macros.blackbox.Context


  def applyTranTo[In: c.WeakTypeTag, To: c.WeakTypeTag](in: c.Expr[In])(tranConfig: c.Expr[TranConfig]): c.Tree = {
    import c.universe._
    val tranMethodList = tranConfig.tree.symbol.typeSignature.members
      .filter(_.info.typeConstructor.contains(c.symbolOf[TranMethod[_, _]]))
      .map(_.asMethod)
    showInfo(showRaw(
      tranMethodList.map(_.info.typeConstructor).head
      //      tranMethodList.map(_.info.typeConstructor).head match {
      //        case tq"macross.tran.TranMethod[$tmIn,$tmTO]" => (tmIn, tmTO)
      //        case tq"macross.tran.TranMethod[$tmIn,$tmTO] with {..$body}" => (tmIn, tmTO)
      //      }
    ))
    q"()"

  }
}
