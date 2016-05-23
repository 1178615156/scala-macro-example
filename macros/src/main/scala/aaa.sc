import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._

q"List.apply[Int](1,2,3)" match {
  case q"$coll.apply[..$t](..$v)" => coll.tpe <:< typeOf[List[_]]
}