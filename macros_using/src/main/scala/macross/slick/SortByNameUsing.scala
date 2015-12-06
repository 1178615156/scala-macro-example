package macross.slick

import macross.slick.UserTable.UserTable
import slick.ast.{ColumnOption, TypedType}
import slick.lifted
import slick.lifted.{ProvenShape, Rep}


import SlickDb.api._


object SortByNameUsing extends App {

  val sortField = "name"

  val a          = UserTable.table.sortBy((e: UserTable) => SortByName.applyDebug(e, sortField, true))
  //等价于
  val b          = UserTable.table.sortBy(e => {
    val f =if (true)
      PartialFunction((name: String) => {
        (name match {
          case (slick_sort_by_name$macro$1@(_: String)) if slick_sort_by_name$macro$1.==("" + ("id")) => e.id.asc
          case (slick_sort_by_name$macro$2@(_: String)) if slick_sort_by_name$macro$2.==("" + ("address_code")) => e.address_code.asc
          case (slick_sort_by_name$macro$3@(_: String)) if slick_sort_by_name$macro$3.==("" + ("name")) => e.name.asc
          case (slick_sort_by_name$macro$4@(_: String)) if slick_sort_by_name$macro$4.==("" + ("mobile")) => e.mobile.asc
        }): slick.lifted.Ordered
      })
    else
      PartialFunction((name: String) => {
        (name match {
          case (slick_sort_by_name$macro$1@(_: String)) if slick_sort_by_name$macro$1.==("" + ("id")) => e.id.desc
          case (slick_sort_by_name$macro$2@(_: String)) if slick_sort_by_name$macro$2.==("" + ("address_code")) => e.address_code.desc
          case (slick_sort_by_name$macro$3@(_: String)) if slick_sort_by_name$macro$3.==("" + ("name")) => e.name.desc
          case (slick_sort_by_name$macro$4@(_: String)) if slick_sort_by_name$macro$4.==("" + ("mobile")) => e.mobile.desc
        }): slick.lifted.Ordered
      })
    f (sortField)
  })

  val prefixName = "aaa."
  User.table join Address.table sortBy {
    case (user, address) =>
      SortByName.applyPrefix(user, true, prefixName) orElse
        SortByName.applyPrefix(address, true, "address.") apply
        sortField
  }

}


