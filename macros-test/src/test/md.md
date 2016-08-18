```scala
class TestMacroAnnotation extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any) = meta {
    defn match {
      case q"..$mod class $name extends ..$base {..$body}" =>
        println(mod)
    }
    defn
  }
}
```

//test

```scala
class MyAnnotation extends scala.annotation.StaticAnnotation

@TestMacroAnnotation
@MyAnnotation
class Hello{
  def a = 1
}
```
scalameta : 1.1.0-SNAPSHOT
 
paradise : 3.0.0-SNAPSHOT

just print `List()`, seem lose the annotation information

