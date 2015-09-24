package macros

import macross.MaxMacros

/**
 * Created by YuJieShui on 2015/9/10.
 */
object MaxUsing extends App{
val a=MaxMacros.apply(1,2)
  assert(a==2)
  println(a)
}
