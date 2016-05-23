package so

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * Created by yuJieShui on 2016/3/24.
  */

trait AssertEquals[T, V] {
  def assertEquals(t: T, v: V): Boolean
}

object AssertEquals {
  implicit def assertEquals[T, V]: AssertEquals[T, V] = macro impl[T, V]


  implicit class WithAssertEquals[T](t: T) {
    def assertEquals[V](v: V)(implicit assertEquals: AssertEquals[T, V]): Boolean = assertEquals.assertEquals(t, v)

    def ass2[V](v: V) :Boolean= macro impl2[T, V]
  }

  def impl2[T: c.WeakTypeTag, V: c.WeakTypeTag](c: Context)(v: c.Expr[V]) = {
    import c.universe._
    val _t = c.weakTypeOf[T]
    val _v = c.weakTypeOf[V]
    q"this.t == v"
  }

  def impl[T: c.WeakTypeTag, V: c.WeakTypeTag](c: Context) = {
    import c.universe._
    val _t = c.weakTypeOf[T]
    val _v = c.weakTypeOf[V]
    q"""
          {
          new ${symbolOf[so.AssertEquals[_, _]]}[${_t},${_v}]{
            def assertEquals(t: ${_t}, v: ${_v}): Boolean = t == v
          }
          }
          """
  }
}


