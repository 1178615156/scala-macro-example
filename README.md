#scala-macro-example
##node
######1
before you use macro must compile complete
so need setting dependsOn like follow also can use jar 
```scala
lazy val macros= project.in(file("module/macros"))

lazy val root = (project in file(".")).dependsOn(macros)
```

####hello
using macro impl hello world 
```scala
  /**
   * must write return type in either hello or helloImpl 
   */
  def hello(s:String):String=macro helloImpl

  def helloImpl(c:Context)(s:c.Expr[String]):c.Expr[String]={
    import c.universe._
    c.Expr(q"""
    "hello:"+$s
    """)
  }
```

####max
it is so ease 
```scala
object MaxMacros {
  def apply(l: Int, r: Int): Int = macro impl

  def impl(c: Context)(l: c.Expr[Int], r: c.Expr[Int]) = {
    import c.universe._
    c.Expr( q"""
    val temp_l=$l
    val temp_r=$r
    if (temp_l>temp_r)
      temp_l
    else
      temp_r

    """)
  }
}
```

####get class 
only a classOf
```scala
object GetClassMacros {
  def apply[T]: Class[T] = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context) = {
    import c.universe._
    c.Expr( q"""
      classOf[${c.weakTypeOf[T]}]
    """)
  }
}
```
