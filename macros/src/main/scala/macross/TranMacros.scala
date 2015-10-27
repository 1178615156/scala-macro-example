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

  case class Replace[From, To](from: From, to: To, func: Tree ⇒ Tree)

  def replaceList = List(
    Replace(
      List(typeOf[Option[_]].typeConstructor, typeOf[Option[_]].typeConstructor),
      List(typeOf[Option[_]].typeConstructor), (name: Tree) ⇒ q"$name.flatten")
  )

  def replaceMap: Map[List[c.universe.Type], List[c.universe.Type]] = replaceList.map(e ⇒ e.from → e.to).toMap

  def replaceFun: Map[List[c.universe.Type], (c.universe.Tree) ⇒ c.universe.Tree] = replaceList.map(e ⇒ e.from → e.func).toMap

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



    def fl[T](l: List[T]): List[List[T]] = {
      l.foldLeft(List[List[T]]()) { (l, r) ⇒
        if (l.isEmpty)
          l :+ List(r)
        else
          l :+ (l.last ::: List(r))
      }
    }

    def exeRep[T](l: List[T],
                  rep: Map[List[T], List[T]],
                  repFunc: Map[List[T], Tree ⇒ Tree]): List[(List[T], (Tree) ⇒ Tree)] = {
      val b = fl(l).reverse.tail.reverse
      val c = fl(l.reverse).reverse.tail.map(_.reverse)

      val d = b zip c
      val rt: List[List[T]] = (d filter (e ⇒ rep.get(e._1).nonEmpty)).map(e ⇒ rep.get(e._1).get ::: e._2)
      val rtFunc =
        (d filter (e ⇒ rep.get(e._1).nonEmpty)).map(e ⇒ repFunc.get(e._1).get)
      if (rt.isEmpty)
        Nil
      else
        rt zip rtFunc
    }
    type HeadList[T] = List[T]

    def r[T](l: List[T], result: List[T],
             rep: Map[List[T], List[T]],
             head: HeadList[T],
             replaceFun: Map[List[T], Tree ⇒ Tree], exeRepList: List[Tree ⇒ Tree]): Option[(List[T], List[(Tree) ⇒Tree])] = {
      if (head ::: l == result)
        Some((head ::: l,exeRepList))
      else
      if (l.isEmpty || l.tail.isEmpty || l.tail.tail.isEmpty)
        None
      else {
        val b: List[(List[T], (c.universe.Tree) ⇒ c.universe.Tree)] = exeRep(l, rep, replaceFun)
        if (b.isEmpty) {
          val cHead: List[List[T]] = head :: fl(l).reverse.tail.reverse ::: fl(l).reverse.tail.reverse
          val c = fl(l.reverse).reverse.tail.map(_.reverse) ::: fl(l.reverse).reverse.tail.map(_.reverse)
          (cHead zip c).map(e ⇒ r(e._2, result, rep, e._1, replaceFun, Nil)).find(_.nonEmpty).flatten

        } else {
          val rt: List[Option[(List[T], List[(c.universe.Tree) ⇒ c.universe.Tree])]] = b.map {
            case (b: List[T], repFuncThis) ⇒
              val cHead = head :: fl(b).reverse.tail.reverse ::: fl(l).reverse.tail.reverse
              val c = b :: fl(b.reverse).reverse.tail.map(_.reverse) ::: fl(l.reverse).reverse.tail.map(_.reverse)
              (head :: cHead zip c).map((e: (List[T], List[T])) ⇒ r(e._2, result, rep, e._1, replaceFun, exeRepList)).find(_.nonEmpty).flatten

          }
          rt.find(_.nonEmpty).flatten
        }


      }

    }
    val o = r(inTypeList, toTypeList, replaceMap, Nil, replaceFun, Nil)

    //    val b = exeRep(inTypeList, inTypeList)
//    showInfo(show(o))

    q"1"
  }
}