package yjs.macrs.utensil

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

trait Param2LogString[Value] extends (Value => String)

object Param2LogString {
  def apply[T](f: T => String): Param2LogString[T] = new Param2LogString[T] {
    override def apply(v1: T): String = f(v1)
  }

  implicit def bySeq[T]: Param2LogString[Seq[T]] = apply(_.size.toString)

  implicit def byInt: Param2LogString[Int] = apply[Int](_.toString)

  implicit def byString: Param2LogString[String] = apply[String](e => e)

  implicit def byAny[T]: Param2LogString[T] = apply(_ => "unknow")


  def show[T: Param2LogString](t: T) = implicitly[Param2LogString[T]].apply(t)

}

class TimeLog extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro TimeLogImpl.apply
}


class TimeLogImpl(val c: blackbox.Context) {

  import c.universe._

  def apply(annottees: Tree*) = {
    val out = annottees.map {
      case x: DefDef =>

        val funcName = x.name.toTermName.toString
        val resultName = TermName(c.freshName("result"))
        val startTimeName = TermName(c.freshName("startTime"))
        val endTimeName = TermName(c.freshName("endTime"))
        val logString = TermName(c.freshName("logString"))

        val params: Seq[Tree] = x.vparamss.flatten.map(_.name).map(name =>
          q"""${name.toString} +"="+ ${c.universe.reify(Param2LogString)}.show($name)""")
        val newBody =
          q"""
             val $startTimeName = System.currentTimeMillis()
             val $resultName = ${x.rhs}
             val $endTimeName = System.currentTimeMillis()
             val $logString =
               "func time: " +
               (($endTimeName - $startTimeName).toDouble/1000).toString +
               " - " +
               $funcName + List(..$params).mkString("(",",",")")

             implicitly[org.slf4j.Logger].info($logString)
             $resultName
           """
//        println(show(newBody))
        val newDef = DefDef(x.mods, x.name, x.tparams, x.vparamss, x.tpt, newBody)
        newDef
    }


    q"..${out}"

  }
}
