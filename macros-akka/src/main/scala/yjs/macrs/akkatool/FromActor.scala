package yjs.macrs.akkatool

import akka.actor.ActorRef

import scala.concurrent.Future
import scala.language.experimental.macros

/**
  * Created by yujieshui on 2016/11/5.
  */
object FromActor {

  def apply[T](actorRef: ActorRef): T = macro FromActorImpl.impl[T]
}


class FromActorImpl(val c: scala.reflect.macros.blackbox.Context) {

  import c.universe._

  def impl[T: c.WeakTypeTag](actorRef: c.Expr[ActorRef]): c.Expr[T] = {
    val t = c.weakTypeOf[T]
    val methods = t.members.filter(_.isAbstract).filter(_.isMethod).map(_.asMethod) //.collect { case x: DefDefApi => x }

    val outMethods = methods.map(method => {
      if(method.paramLists.size != 1 || method.paramLists.head.size != 1) c.abort(c.enclosingPosition, "method params must as 1 ")

      val param = method.paramLists.head.head
      val returnType = method.returnType
      def asFuture = method.returnType <:< typeOf[Future[_]]
      def dropFutureWeak = returnType.typeArgs.tail.foldLeft(tq"${returnType.typeArgs.head}") { (l, r) => tq"${l}[${r}]" }
      if(!asFuture) c.abort(c.enclosingPosition, s"method return type must as Future[...] : ${returnType}")
      val askMapToType = if(asFuture) dropFutureWeak else tq"${returnType}"

      val params = method.paramLists.map(_.map(e => q"${e.name.toTermName}:${e.typeSignature}"))
      val body =q"""akka.pattern.ask($actorRef).ask(${param.name.toTermName}).mapTo[${askMapToType}]"""
      q"override def ${method.name}(...${params}):${returnType}= {$body} "
    })
    println(outMethods.mkString("\n"))
    c.Expr[T](
      q"""
       new $t {
        ..${outMethods}
       }
      """
    )
  }
}

