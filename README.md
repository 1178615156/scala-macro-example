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

