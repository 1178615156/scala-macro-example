def myMethod(x: String)(y: Int) = ((x0$1: String, x1$1: Any) => scala.Tuple2.apply[String, Any](x0$1, x1$1) match {
  case scala.Tuple2("myMethod", (args @ _)) => "ok"
  |})("myMethod", List(x, y)).asInstanceOf[String]
