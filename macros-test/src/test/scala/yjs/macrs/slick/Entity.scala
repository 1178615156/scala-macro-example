package yjs.macrs.slick
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape

/**
  * Created by yujieshui on 2017/5/12.
  */
case class Entity(id: Int, name: String, helloWorld: Option[String])


case class EntityTable(tag: Tag) extends Table[Entity](tag, "entity") {
  val id          : Rep[Int]            = column[Int]("id", O.PrimaryKey)
  val name        : Rep[String]         = column[String]("name")
  val passwordHash: Rep[Option[String]] = column[Option[String]]("helloWorld")

  override def * : ProvenShape[Entity] = (id, name, passwordHash) <> (Entity.tupled, Entity.unapply)
}
