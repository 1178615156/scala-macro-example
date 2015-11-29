package macross.slick

import slick.lifted.ProvenShape
import macross.slick.UserTable.UserTable
import slick.ast.{ColumnOption, TypedType}
import slick.lifted.{ProvenShape, Rep}



import SlickDb.api._


/**
  * Created by yjs on 2015/11/28.
  */
case class Address(code: Int)
object Address {

  trait Column {
    self: Table[Address] =>
    val code = column[Int]("code")

    def * : ProvenShape[Address] = SlickStarMacro.apply[Column, Address]
  }

  class TB(tag: Tag) extends Table[Address](tag, "user") with Column

  val table = TableQuery[TB]
}
