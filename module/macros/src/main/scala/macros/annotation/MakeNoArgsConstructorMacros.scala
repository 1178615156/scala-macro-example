package macros.annotation

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

/**
 * Created by YuJieShui on 2015/9/11.
 */
class MakeNoArgsConstructorMacros extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro MakeNoArgsConstructorMacrosImpl.impl

}

class MakeNoArgsConstructorMacrosImpl(val c: Context)
//let us reuse code
//only has a getInClass
//we already write it in MakeGetSetImpl
//so no copy yet reuse it
  extends base.GetInClassBase
  with base.ClassWithFuncBase {

  import c.universe._

  def impl(annottees: c.Expr[Any]*): c.Expr[Nothing] = {
    val inClass = getInClass(annottees.map(_.tree).toList)
    val out=inClass match {
      case q"$mod class $name(..$params) extends ..$base {..$body}" =>
        val defaultCtorPos = c.enclosingPosition

        val newCtorPos = defaultCtorPos
          .withEnd(defaultCtorPos.end + 1)
          .withStart(defaultCtorPos.start + 1)
          .withPoint(defaultCtorPos.point + 1)
        val newCtor = q"def this() = {this(..${params.map(e ⇒ q"null.asInstanceOf[${e.asInstanceOf[ValDef].tpt}]")})}"
        classWithFunc(inClass,
          List(atPos(newCtorPos)(newCtor)
          ).asInstanceOf[List[Tree]]
        )
    }
    c.Expr(out)
  }

}
