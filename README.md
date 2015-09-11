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
#### get public val 
collect public val to list and map 
like follow
```scala
  object Module_1 {
    val a = 1
    val b = 2
    val c = 3
    //need write return type
    val list: List[Int] = GetPublicValMacros.listValue[Module_1.type, Int]//List(1,2,3)
    val map: Map[String, Int] = GetPublicValMacros.mapValue[Module_1.type, Int]//Map(c -> 3, b -> 2, a -> 1)
  }
```