package macross

/**
  * Created by yjs on 2015/11/17.
  */
object SO33756799Using extends App{

  import SO33756799._

  trait TheTrait {
    def myMethod(x: String)(y: Int): String
  }

  val proxy: TheTrait = ProxyMacro.proxy[TheTrait] {
    case ("myMethod", args) =>
      "ok"
  }
  println(proxy.myMethod("hello")(5))
}
