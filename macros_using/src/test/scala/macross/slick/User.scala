package macross.slick

import macross.slick.UserTable.UserTable
import slick.ast.{ColumnOption, TypedType}
import slick.lifted.{ProvenShape, Rep}



import SlickDb.api._


/**
  * Created by yjs on 2015/11/28.
  */
case class User(
                 mobile: String,
                 name: Option[String],
                 address_code: Int,
                 id: Long
               )
object User{
  val table = UserTable.table
}
object UserTable {
  val table = TableQuery[UserTable]

//  trait Column {
//    self: Table[User] =>
//
//  }

  class UserTable(tag: Tag) extends Table[User](tag, "user") {
    val mobile = column[String]("mobile")
    val name = column[Option[String]]("name")
    val address_code = column[Int]("")
    val id = column[Long]("id", O.PrimaryKey)

    val address = foreignKey("", address_code, Address.table)(_.code)

    def * : ProvenShape[User] = ??? //SlickStarMacro.apply[UserTable, User]
//    def * ={
//      import slick.collection.heterogeneous._;
//      new HCons(keyword, new HCons(user_id, HNil)).shaped.$less$greater(<empty> match {
//      case (hlist @ _) => SearchWordHistory(hlist(0), hlist(1))
//      }, ((model: macross.slick.SearchWordHistory) => Option(new HCons(model.keyword, new HCons(model.user_id, HNil)))))
//      }
  }
  def apply() = table
}


