i think it need use macro-annotation  http://docs.scala-lang.org/overviews/macros/annotations.html 
```scala
case class TypeClass(name: String, bla: Option[String])
//we need write like following  
//i think it is not good :( 
@Macro.reads[TypeClass]
implicit def GetResultTypeClass = ???
```
