# scala-macro-example
[![Build Status](https://travis-ci.org/1178615156/scala-macro-example.svg?branch=meta-impl)](https://travis-ci.org/1178615156/scala-macro-example)
```scala 
  lazy val `scala-macro-example` =
    ProjectRef( uri("git:https://github.com/1178615156/scala-macro-example"),"scala-macro-example")
```
#### conf parsing config path in complete 
```scala
import macross.conf.conf 
object global_conf {
  implicit val config: Config = ConfigFactory.load()
  @conf
  object hello {
    val name  = config.getString(conf.path /* == "hello.name" */)
    val world = config.getLong(conf.path/* == "hello.world" */).second.toMillis
    val ss    = conf.as[String]//config.getString("hello.ss")
    val ll    = conf.as[List[Int]] //config.getIntList("hello.ll")

  }

}
```
