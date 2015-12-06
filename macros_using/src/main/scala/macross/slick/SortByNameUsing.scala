package macross.slick

import macross.slick.UserTable.UserTable
import slick.ast.{ColumnOption, TypedType}
import slick.lifted
import slick.lifted.{ProvenShape, Rep}


import SlickDb.api._


object SortByNameUsing extends App {

  val sortField = "name"
  val l         = List("id")

  val a          = UserTable.table.sortBy((e: UserTable) => SortByName.applyDebug(e, l.head, true))

  "" match {
    case e if e.equals("" + "")=>
  }

  //等价于
  val b          = UserTable.table.sortBy(e => {

    (sortField match {
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
  val prefixName = "aaa."
  User.table join Address.table sortBy {
    case (user, address) =>
      SortByName.applyPrefix(user, true, prefixName) orElse
        SortByName.applyPrefix(address, true, "address.") apply
        sortField
  }

}

