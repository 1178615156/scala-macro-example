# scala-macro-example
src/main is use

module/macros is macro impl
```
show info               when you debug or test feature also is useful 
get annotation param    get annotation param
get public val          collect public val to list and map 
make get set            using macro annotation make get set method 
make constructor        using macro annotation make no args constructor
    
```

#### show info 
####get annotation param
 when you use idea 
 open bottom terminal 
 enter 
 ```scala
//when you change or write code sbt will compile your project 
 sbt ~compile  //compile project no include test 
 sbt ~test     //compile project include test
 ```

```scala
  trait SuperTrait
  class SuperClass
  @ShowInfo.Show
  //@ShowInfo.showCode
  //@ShowInfo.ShowRaw(showInfo=false)
  class ShowInfoUsing(val i: Int = 1) extends SuperClass with SuperTrait {
    def f = 1

    val a = 1
  }
```
try remove annotation prefix //, keydown ctrl+s 
 you well look compile info 
 and try change (showInfo=false) to (showInfo=true)
 

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
##full name 
 get package + class name 
```scala
@FullNameMacro
class FullNameUsingEntity
  val fn=new FullNameUsingEntity().fullName
  assert(fn=="macros.annotation.FullNameUsingEntity")
```

