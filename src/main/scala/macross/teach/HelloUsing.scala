package macross.teach

/**
 * Created by YuJieShui on 2015/9/10.
 */
object HelloUsing extends App{
  val a=HelloMacros.apply("world")//hello:world
  println(a)
  assert(a=="hello:world")
}
