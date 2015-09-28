package macross.annotation

import macross.annotation.FullNameMacro

/**
 * Created by yu jie shui on 2015/9/14 15:42.
 */
@FullNameMacro
case class FullNameUsingEntity()
object FullNameUsing extends App{
  val fn=new FullNameUsingEntity().fullName
  assert(fn=="macros.annotation.FullNameUsingEntity")
  println(fn)
}

