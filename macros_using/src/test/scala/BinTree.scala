import scala.beans.BeanProperty
import scala.language.implicitConversions

/**
 * Created by YuJieShui on 2015/9/14.
 */
case class BinTree[T](value: T, left: Option[BinTree[T]] = None, right: Option[BinTree[T]] = None)

object BinTree {
  implicit def binTree2OptionBinTree[T](binTree: BinTree[T]): Some[BinTree[T]] = Some(binTree)
}

