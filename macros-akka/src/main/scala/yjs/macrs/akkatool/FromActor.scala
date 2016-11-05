package yjs.macrs.akkatool

import akka.actor.ActorRef
import yjs.macrs.common.TypeUtils

import scala.concurrent.Future
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by yujieshui on 2016/11/5.
  */
object FromActor {
  def apply[T](actorRef: ActorRef): T = macro FromActorImpl.impl[T]
}


class FromActorImpl(override val c: blackbox.Context) extends TypeUtils {

  import c.universe._

  def getAbstractMethod(tye: Type) =
    tye.members.filter(_.isAbstract).filter(_.isMethod).map(_.asMethod)

  def impl[T: c.WeakTypeTag](actorRef: c.Expr[ActorRef]): c.Expr[T] = {
    val t = c.weakTypeOf[T]
    val abstractMethods = getAbstractMethod(t)

    val outMethods = abstractMethods.map(method => {
      if(method.paramLists.size != 1 || method.paramLists.head.size != 1) c.abort(c.enclosingPosition, "method params must as 1 ")

      val param = method.paramLists.head.head
      val returnType = method.returnType

      val params = method.paramLists.map(_.map(e => q"${e.name.toTermName}:${e.typeSignature}"))

      val body = returnType match {
        case e if asUnit(e)   => q"$actorRef.!(${param.name.toTermName})"
        case e if asFuture(e) => q"akka.pattern.ask($actorRef).ask(${param.name.toTermName}).mapTo[${dropWeak(returnType)}]"
        case e                => c.abort(c.enclosingPosition, "method return type is not [unit or future]")
      }

      q"override def ${method.name}(...${params}):${returnType}= {$body} "
    })
    c.echo(c.enclosingPosition, outMethods.map(e => show(e)).mkString("\n", "\n", ""))
    c.Expr[T](
      q"""
       new $t {
        ..${outMethods}
       }
      """
    )
  }

}

