package yjs.macrs.akkatool

import akka.actor.ActorRef
import yjs.macrs.common.TypeUtils

import scala.concurrent.{ExecutionContext, Future}
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

/**
  * Created by yujieshui on 2016/11/5.
  */
@deprecated("use TypeSafeActor", "")
object FromActor {
  @deprecated("use TypeSafeActor", "")
  def apply[T](actorRef: ActorRef): T = macro TypeSafeActorImpl.fromActor[T]
}

object TypeSafeActor {
  def fromActor[T](actorRef: ActorRef): T = macro TypeSafeActorImpl.fromActor[T]

  def receive[Api](api: Api)(implicit ec: ExecutionContext): PartialFunction[Any, Unit] = macro TypeSafeActorImpl.receive[Api]
}

class TypeSafeActorImpl(override val c: blackbox.Context) extends TypeUtils {

  import c.universe._

  def getAbstractMethod(tye: Type) =
    tye.members.filter(_.isAbstract).filter(_.isMethod).map(_.asMethod)

  def requireMethod(method: MethodSymbol) =
    if(method.paramLists.size != 1 || method.paramLists.head.size != 1) c.abort(c.enclosingPosition, "method params must as 1 ")

  //
  def receive[Api: c.WeakTypeTag](api: c.Expr[Api])(ec: c.Expr[ExecutionContext]): c.Expr[PartialFunction[Any, Unit]] = {
    val t = c.weakTypeOf[Api]

    val abstractMethods = getAbstractMethod(t)
    val outMethods: Iterable[c.universe.Tree] = abstractMethods.map { method =>
      requireMethod(method)
      val param = method.paramLists.head.head
      val paramType = param.typeSignature
      val returnType = method.returnType

      returnType match {
        case e if asUnit(e)   => cq" x:$paramType => ${api}.${method.name}(x)"
        case e if asFuture(e) => cq" x:$paramType => akka.pattern.pipe(${api}.${method.name}(x)).pipeTo(sender())"
        case e                => c.abort(c.enclosingPosition, "method return type is not [unit or future]")
      }
    }
    val out = q"PartialFunction[Any,Unit]{case ..$outMethods}"

    c.echo(c.enclosingPosition, out.toString())
    c.Expr[PartialFunction[Any, Unit]](out)
  }


  //
  def fromActor[T: c.WeakTypeTag](actorRef: c.Expr[ActorRef]): c.Expr[T] = {
    val t = c.weakTypeOf[T]
    val abstractMethods = getAbstractMethod(t)

    val outMethods = abstractMethods.map(method => {
      requireMethod(method)

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
    val out =
      q"""
       new $t {
        ..${outMethods}
       }
      """
    c.echo(c.enclosingPosition,show(out))
    c.Expr[T](out)
  }

}

