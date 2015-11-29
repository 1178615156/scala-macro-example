package macross.tran

import macross.slick.{SlickModelColumnSwap, SlickStarMacro}
import slick.driver.MySQLDriver.api._
import slick.lifted.ProvenShape


/**
  * Created by yu jie shui on 2015/11/26 16:35.
  */
case class SearchWordHistory(
                              keyword: List[String],
                              user_id: Option[String]
                            )

object SearchWordHistoryTable {

  class EntityTable(tag: Tag) extends Table[SearchWordHistory](tag, "aaa")
  with Column

  trait Column {
    self: Table[SearchWordHistory] ⇒
    val keyword = column[String]("keyword")
    val user_id = column[Option[String]]("user_id", O.Length(32))

    implicit def keyword_list2string(s: List[String]) = s.toString()

    implicit def keyword_string2list(i: String) = List(i)

    override def * : ProvenShape[SearchWordHistory] = SlickStarMacro.apply[Column, SearchWordHistory]
  }

}

object TimesingUsing extends App {

}
