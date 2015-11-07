# scala-macro-example
src/main is use

module/macros is macro impl
```
tran                    auto type tran
get public val          collect public val to list and map
make get set            using macro annotation make get set method 
make constructor        using macro annotation make no args constructor

```
#### auto type tran
```scala

  object a {
    val value = Option(Option(1))
    val need_result: Option[Int] = value.flatten
    type To = Option[Int]
  }

  object b {
    val value: Option[Future[Option[Future[List[Int]]]]] = Option(Future(Option(Future(List(2)))))
    val need_result: Future[Option[List[Int]]] =
      value.traverse.map(_.flatten).map(_.traverse).flatMap(e ⇒ e)
    type To = Future[Option[List[Int]]]
  }

    import Data._

// use like this

    assert(
      a.value.tranTo[a.To] == a.need_result
    )
    assert(Await.result(
      b.value.tranTo[b.To] zip b.need_result map (e ⇒ e._1 == e._2)
      , Inf)
    )
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
use like
```scala
  @MakeGetSet
  case class Module(
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
  case class Module(
                    i: Int, 
                    s: String)

  val m = new Module()
  println(m.getI) //0
  println(m.getS) //null
```

