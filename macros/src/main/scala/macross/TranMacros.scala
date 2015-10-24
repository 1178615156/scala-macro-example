package macross

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

/**
 * Created by YuJieShui on 2015/10/4.
 */
object TranMacros {

  def apply[In, To](in: In): Any = macro TranMacrosImpl.apply[In, To]

  def typeList[T]: List[String] = macro TranMacrosImpl.typeListTest[T]
}

class TranMacrosImpl(val c: Context)
  extends base.ShowInfo {
  self =>

  import c.universe._

  case class Replace[From, To](from: From, to: To)

  def replaceList = List(
    Replace(
      List(typeOf[Option[_]].typeConstructor, typeOf[Option[_]].typeConstructor),
      typeOf[Option[_]].typeConstructor)
  )

  def replaceMap: Map[List[c.universe.Type], c.universe.Type] = replaceList.map(e ⇒ e.from → e.to).toMap

  def typeParamsList[T: c.WeakTypeTag]: List[Type] = {
    def getTypeList(it: Type): List[Type] =
      if (it.typeArgs.isEmpty)
        List(it)
      else
        it.typeConstructor +: it.typeArgs.flatMap(getTypeList)
    getTypeList(c.weakTypeOf[T])
  }

  def typeListTest[T: c.WeakTypeTag] =
    c.Expr[List[String]](q"""${typeParamsList[T].map(_.toString)}""")

  def apply
  [In: c.WeakTypeTag, To: c.WeakTypeTag]
  (in: c.Expr[In]): Tree = {
    val inTypeList = typeParamsList[In]
    val toTypeList = typeParamsList[To]
    val replaceMaxSize = replaceMap.keys.maxBy(_.productArity)


    def fl[T](l: List[T]) = l.foldLeft(List[List[T]]()) { (l, r) ⇒
      if (l.isEmpty)
        l :+ List(r)
      else
        l :+ (l.last ::: List(r))
    }
    val b = fl(inTypeList).reverse.tail.reverse
    showInfo(show(b))

    q"1"
    //
    //    val inType = c.weakTypeOf[In]
    //    val toType = c.weakTypeOf[To]
    //
    //
    //    def getTypeList(it: Type): List[Type] = {
    //      if (it.typeArgs.isEmpty)
    //        List(it)
    //      else
    //        it.typeConstructor +: it.typeArgs.flatMap(getTypeList)
    //    }
    //
    //    val inTypeList = getTypeList(inType)
    //    showInfo(show(inTypeList))
    //
    //
    //
    //    val toTypeList = getTypeList(toType)
    //    //    showInfo(show(listToListTuple(inTypeList)()))
    //
    //    val ttv: Map[(c.universe.Type, c.universe.Type), c.universe.Type] = Map(
    //      (typeOf[Option[_]].typeConstructor -> typeOf[Option[_]].typeConstructor
    //        ) -> typeOf[Option[_]].typeConstructor
    //    )
    //    //    showInfo(showRaw(inTypeList.head))
    //
    //    //    showInfo(showRaw(c.universe.typeOf[Option[_]]))
    //    //    showInfo(show(listToListTuple(inTypeList).map(e => ttv.get(e))))
    //    def t(inTypeList: List[Type], toTypeList: List[Type]): Boolean = {
    //      if (inTypeList.size == toTypeList.size &&
    //        inTypeList.zip(toTypeList).forall(t2 => t2._1 <:< t2._2)
    //      ) {
    //        showInfo(show(toTypeList) + " == " + show(toTypeList))
    //        true
    //      }
    //      else {
    //        def listToListTuple[V](list: List[V], rt: List[Tuple2[V, V]] = Nil): List[Tuple2[V, V]] = {
    //          if (list.isEmpty || list.tail.isEmpty)
    //            rt
    //          else {
    //            listToListTuple(list.tail, rt :+ (list.head -> list.tail.head))
    //          }
    //        }
    //        //        def listChild(list: List[Type], rt: List[List[Type]]) = {
    //        //          list.tail.foldLeft(list.head -> 0 -> rt) { (l, r) => {
    //        //            val a: Option[c.universe.Type] =ttv.get(l._1._1 -> r)
    //        //            if (a.isEmpty)
    //        //            r -> (l._1._2 + 1)->l._2
    //        //            else {
    //        //              l._2.++(list)
    //        //            }
    //        //          }
    //        //          }
    //        //        }
    //        //        inTypeList.foldLeft()
    //        listToListTuple(inTypeList).map(e => ttv.get(e))
    //        showInfo(show(toTypeList) + " != " + show(toTypeList))
    //        false
    //      }
    //      //      showInfo(show(inType.typeArgs))
    //      //      showInfo(show(
    //      //        inType.typeConstructor
    //      //      ))
    //      //        if (inType <:< toType) {
    //      //          showInfo(show(inType) + " == " + show(toType))
    //      //          true
    //      //        } else {
    //      //          false
    //      //        }
    //      //      else if (inType.typeArgs.size < 1) {
    //      //        c.error(c.enclosingPosition,
    //      //          show(inType) + " un tran to " + show(toType)
    //      //        ).asInstanceOf[Tree]
    //      //      } else {
    //      //
    //      //        t(inType, inValue)
    //      //      }
    //      //      if (inType <:< toType) {
    //      //        showInfo(show(inType) + " == " + show(toType))
    //      //        inValue
    //      //      } else {
    //      ////        inType.map(e=>e.typeArgs)
    //      //
    //      //        inValue
    //      //      }
    //
    //
    //    }
    //    t(inTypeList, toTypeList)
    //    in.tree
    //    //    showInfo(inType <:< c.typeOf[Option[Option[_]]] toString)
    //    //    tq"$inType" match {
    //    //      case tq"Option[Option[Int]]" =>
    //    //    }
    //    //    showInfo(show(inType<:<c.typecheck(tq"Option[Option[Int]]").tpe))
    //
    //    //    if (inType == toType) {
    //    //      showInfo("true")
    //    //    } else {
    //    //      showInfo("false")
    //    //    }
    //    //    showInfo(show(c.weakTypeOf[In]))
    //
    //    //    q"""
    //    //        1
    //    //    """
    //    //    in.tree
  }
}