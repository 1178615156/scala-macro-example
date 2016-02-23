package macross.slick

import slick.jdbc.{PositionedResult, GetResult}
import slick.lifted.ProvenShape

/**
  * Created by yuJieShui on 2016/1/26.
  */
class GetResultMacroTest {

  import slick.driver.MySQLDriver.api._

  case class Address(street: String, city: String, code: Option[Int])

  case class User(id: Long, name: String, age: Option[Int], address: Address, address2: Option[Address])

//  implicit val implicitGetResultAddress = GetResultMacro.apply[Address]
//
//  implicit val implicitGetResultUser =
//    GetResultMacro.apply[User]
//
//
//  sql"SELECT * FROM User WHEN TRUE ".as[User]

}
