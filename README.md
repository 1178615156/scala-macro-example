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
only say hello world
```scala
  val a=HelloMacros.apply("world")//hello:world
```

####max
get max val
```scala
  val a=MaxMacros.apply(1,2)
```
 node use a temp value bind the function params , like follow
```scala
    c.Expr( q"""
    val temp_l=$l
    val temp_r=$r
    if (temp_l>temp_r)
      temp_l
    else
      temp_r
    """)
```

####get class 
only a classOf
```scala
  val a: Class[String] = GetClassMacros.apply[String]
```
impl is so easy
```scala
    c.Expr( q"""
      classOf[${c.weakTypeOf[T]}]
    """)
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
#### using macro annotation make get set method 
##### if you use hibernate then combination make-no-args-constructor is very useful 
use like 
```scala
  @MakeGetSet
  //Entity
  case class Module(//Id
                     i: Int = 2,
                     s: String,
                     o: Option[String],
                     n: Option[AnyRef] = None
                     )

  val a = new Module(s = "sss", o = Some("option"))
  println(a.getI)//2
  println(a.getO)//option
  println(a.getN)//null
```
####make-no-args-constructor
use like follow 
```scala
  @MakeNoArgsConstructorMacros
  case class Module(i: Int, s: String)

  val m = new Module()
  println(m.i)//0
  println(m.s)//null
```