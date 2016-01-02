package so

/**
  * Created by yuJieShui on 2015/12/31.
  */
class GetFunctionAnnotation {

}

import scala.annotation.StaticAnnotation
import scala.language.experimental.macros

case class Name[T](name: String)

object Name {

  implicit def name[T]: so.Name[T] = macro NameMacro.materialize[T]
}

class transform(value: String => String) extends StaticAnnotation

object NameMacro {

  def materialize[T: c.WeakTypeTag](c: scala.reflect.macros.blackbox.Context): c.universe.Tree = {
    import c.universe._

    val tag = implicitly[WeakTypeTag[T]]
    val nameType = c.typeOf[so.Name[_]].typeSymbol.name.toTypeName
    val name = tag.tpe.typeSymbol.name.toString
    val annotation = tag.tpe.typeSymbol.annotations.find(_.tree.tpe <:< weakTypeOf[transform])
    val transformation = annotation.map(_.tree.children.tail.head)
    val transformed: c.universe.Tree = transformation.map(f => q"$f.apply($name)").getOrElse(q"$name")

    import c.universe.Flag._

    val code =
      q"""
                  {
                  val i =new Name[${TypeName(c.weakTypeOf[T].typeSymbol.name.toString): TypeName}]( $transformed )
                  i
                  }
      """
    //    c.abort(c.enclosingPosition, showRaw(code))
    //        c.abort(c.enclosingPosition, showCode(code))
    println(code)
    code
  }
}