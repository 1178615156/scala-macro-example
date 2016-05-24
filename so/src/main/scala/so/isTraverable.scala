package so

import scala.collection.TraversableOnce
import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/**
  * Created by yuJieShui on 2016/3/16.
  */
    object isTraverable {
      def apply[V](v: V): Any = macro impl[V]

      def impl[V: c.WeakTypeTag](c: whitebox.Context)(v: c.Expr[V]) = {
        import c.universe._

        def isTraverable(coll: Tree) = {
          coll.tpe.typeConstructor <:< typeOf[TraversableOnce[_]].typeConstructor
        }

        def pattern(tree: Tree): Unit = tree match {

          // NODE : coll.tye is not typeOf[List[_]] but rather typeOf[List.type]

          case q"$coll.apply[..$t](..$agrs)" if isTraverable(tree) =>
            println(s"${coll} <:< TraversableOnce")
            pattern(agrs.last)
          case other =>
            println(s"$other is not TraversableOnce")
        }

        //immutable.this.List <:< TraversableOnce
        //immutable.this.List <:< TraversableOnce
        //immutable.this.List <:< TraversableOnce
        //3 is not TraversableOnce
        pattern(v.tree)

        q"()"
      }
    }
