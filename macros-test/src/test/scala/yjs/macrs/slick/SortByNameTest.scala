package yjs.macrs.slick

import slick.jdbc.MySQLProfile.api._

/**
  * Created by yujieshui on 2017/5/12.
  */
object SortByNameTest {
  val sortFieldName                  = "id"
  val table: TableQuery[EntityTable] = TableQuery[EntityTable]

  table.sortBy(table => SortByName.apply(table, sortFieldName, true))
}


