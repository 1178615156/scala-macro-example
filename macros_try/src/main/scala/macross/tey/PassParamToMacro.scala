package macross.tey

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
 * Created by yu jie shui on 2015/11/6 17:06.
 */
object PassParamToMacro {

  implicit class WithHello[T](val s: T) {
    def hello(name: String)(implicit helloImpl: Hello[T]): String = helloImpl.hello(s, name)
  }

  trait Hello[T] {
    def hello(s: T, name: String): String
  }

  object Hello {
    implicit def implicitHelloImpl[T]: Hello[T] = macro HelloImpl.hello[T]
  }


  class HelloImpl(val c: Context) extends macross.base.ShowInfo {

    import c.universe._

    def hello[T: c.WeakTypeTag]: c.Tree = {
      val t = c.weakTypeOf[T]
      q"""
          new Hello[${t}]{
            def hello(s:$t,name:String):String=s.toString+name
          }
          """
    }
  }
}
