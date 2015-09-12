# scala-macro-example
src/main is use

module/macros is macro impl
## node
###### 1
before you use macro must compile complete
so need setting dependsOn like follow also can use jar 
```scala
lazy val macros= project.in(file("module/macros"))

lazy val root = (project in file(".")).dependsOn(macros)
```
###### 2
if you use macro annotation then need add follow to build.sbt
```scala
val paradiseVersion = "2.1.0-M5"

crossScalaVersions := Seq("2.10.2", "2.10.3", "2.10.4", "2.10.5", "2.11.0", "2.11.1", "2.11.2", "2.11.3", "2.11.4", "2.11.5", "2.11.6", "2.11.7")

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _)
```

#### hello
using macro impl hello world 
only say hello world
```scala
  val a=HelloMacros.apply("world")//hello:world
```

#### max
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

#### get class
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
## use work sheet
node:only idea have work sheet ;
     scala ide maybe has same feature
when you test macro feature then the work sheet is very useful so let us see it :
code in module/src/main/scala/ws
first new a ScalaWorkSheet
when you use macro then you need add follow 
```scala
import scala.language.experimental.macros
val universe: scala.reflect.runtime.universe.type =
  scala.reflect.runtime.universe
import universe._
```
after copy and run it 
```scala
q"val a:Int=1"
q"def a:Int=1"
q"class A"
q"object A"
q"trait A"
```
#### DefDef explanation
show DefDef frequently used function
code see to ws/DefDef_explanation

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
#### make-no-args-constructor
use like follow 
```scala
  @MakeGetSet
  @MakeNoArgsConstructorMacros
  //Entity
  case class Module(//@id
                    i: Int, 
                    s: String)

  val m = new Module()
  println(m.getI) //0
  println(m.getS) //null
```