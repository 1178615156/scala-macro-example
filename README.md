# macros-utils
[![Build Status](https://travis-ci.org/1178615156/scala-macro-example.svg?branch=master)](https://travis-ci.org/1178615156/scala-macro-example)

```scala
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M5" cross CrossVersion.full)
```

#### slick

```scala
case class Entity(id: Int, name: String, helloWorld: Option[String])


case class EntityTable(tag: Tag) extends Table[Entity](tag, "entity") {
  val id          : Rep[Int]            = column[Int]("id", O.PrimaryKey)
  val name        : Rep[String]         = column[String]("name")
  val passwordHash: Rep[Option[String]] = column[Option[String]]("helloWorld")

  override def * : ProvenShape[Entity] = (id, name, passwordHash) <> (Entity.tupled, Entity.unapply)
}
```

SortByName
```scala
  val sortFieldName                  = "id"
  val table: TableQuery[EntityTable] = TableQuery[EntityTable]

  table.sortBy(table => SortByName.apply(table, sortFieldName, true))
  //等价于
  table.sortBy(table => sortFieldName match {
    case "id"           => if(true) table.id.asc else table.id.desc
    case "name"         => if(true) table.name.asc else table.name.desc
    case "passwordHash" => if(true) table.passwordHash.asc else table.passwordHash.desc
  })
```

GetResult
```scala
GetResultBuild.literal[Entity]
//等价于
slick.jdbc.GetResult[Entity]((x :slick.jdbc.PositionedResult ) => new Entity(...))
```
#### make play routes

it will auto make conf/routes file 

```scala
package controllers

import javax.inject.{Inject, Singleton}

import play.api._
import play.api.mvc._
import yjs.macrs.play.MakeRoute
import yjs.macrs.play.Routes._

@MakeRoute
@Path("/hello")
@Singleton
class Application @Inject()
() extends Controller {

  @Get("/index")
  @Get("/index2")
  def index(i:Int,s:String) = Action {
    Ok("1")
  }

  @Post("/hello")
  @Get("/xxx")
  def hello = Action(Ok("2"))

}
```

the conf/routes file it like this
```
GET     /hello/index   controllers.Application.index(i:Int,s:String)
GET     /hello/index2  controllers.Application.index(i:Int,s:String)
POST    /hello/hello   controllers.Application.hello
GET     /hello/xxx     controllers.Application.hello
```