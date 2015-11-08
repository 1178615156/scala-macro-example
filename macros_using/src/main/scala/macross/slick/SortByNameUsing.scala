package macross.slick

import slick.lifted.Rep

/**
  * Created by yu jie shui on 2015/11/8 20:21.
  */
object SlickDb {
  val api: slick.driver.MySQLDriver.api.type = slick.driver.MySQLDriver.api
  Rep
}
import SlickDb.api._

case class User(
                 mobile: String,
                 name: Option[String],
                 id: Long
               )

class UserTable(tag: Tag)
  extends Table[User](tag, "user") {

  val mobile = column[String]("mobile")
  val name = column[Option[String]]("name")
  val id = column[Long]("id", O.PrimaryKey)

  def * = (mobile, name, id) <>(
    User.tupled, User.unapply
    )
}

object UserTable {
  val table = TableQuery[UserTable]

  def apply() = table
}

object SortByNameUsing extends App {

  val b = UserTable.table.sortBy((e: UserTable) => SortByName.apply(e, "name", true))
  //等价于
  val a = UserTable.table.sortBy(e => {
    ("name" match {
      case "id" => if (true)
        e.id.asc
      else
        e.id.desc
      case "name" => if (true)
        e.name.asc
      else
        e.name.desc
      case "mobile" => if (true)
        e.mobile.asc
      else
        e.mobile.desc
    }): slick.lifted.Ordered
  })
}
