package macross.annotation

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

import macross.base.ShowInfo

/**
  * Created by yuJieShui on 2016/1/7.
  */
class TailCall[T](val zero: T) extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro TailCallImpl.apply
}

class TailCallImpl(val c: Context) extends ShowInfo with macross.annotation.base.AnnotationParam {

  import c.universe._

  def apply(annottees: c.Expr[Any]*): c.Expr[Any] = {

    val zero = this.annotationParams.head.head
    val rtName = TermName(c.freshName("rt"))
    val q"""
          def ${funcName: TermName}(...${params: List[List[ValDef]]}):${Ident(resultType: TypeName)}=
          {..${body: List[Tree]}}
      """ = annottees.head.tree.duplicate
    val last = body.last




    def isIfElseStruct(tree: Tree) = tree match {
      case q"if ($base) $t else $el " ⇒ true
    }
    if (!isIfElseStruct(last))
      c.abort(c.enclosingPosition, "tail call only support if else")

    //todo
    def isTailRec(tree: Tree) = ???
    //todo
    def isCaseStruct = ???

    def getElse(tree: Tree): Tree = tree match {
      case q"if ($base) $t else $el " ⇒ getElse(el)
      case t: Tree ⇒ t
    }

    val els = getElse(last)
    val lastOperation = els match {
      case q"$f.$m(${Ident(recFunc: TermName)}(...$p))" if recFunc == funcName ⇒
        m
    }
    val tailRecName = TermName(c.freshName("tailRecursiveImpl"))
import scala.util.control.TailCalls
    def writeTo(tree: Tree): Tree = tree match {
      case q"if ($bool) $eof else $rec" ⇒
        q"if ($bool) $rtName.$lastOperation($eof) else ${writeTo(rec)}"
      case els: Tree ⇒ els match {
        case q"$f.$m(${Ident(recFunc: TermName)}(...$p))" if recFunc == funcName ⇒
          q"$tailRecName(...$p)($f.$m($rtName))"
      }
    }
    val tailRec =
      q"""
          def $tailRecName (...${params})($rtName : $resultType):$resultType = ${writeTo(last)}
          """

    val out =
      q"""
          def ${funcName}(...${params}):${resultType} = {
            ..${body.reverse.tail.reverse}
            $tailRec
            $tailRecName(...$params)($zero)
          }
      """
    showInfo(showCode(out))
    c.Expr(q"{..${annottees}}")
  }


}