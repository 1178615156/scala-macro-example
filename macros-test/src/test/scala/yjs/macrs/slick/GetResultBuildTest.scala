package yjs.macrs.slick

import slick.lifted.ProvenShape
import slick.jdbc.MySQLProfile.api._


class GetResultBuildTest {
  GetResultBuild.literal[Entity]

}

object SortByNameTest {
  val sortFieldName = "id"
  val table = TableQuery[EntityTable]

  SortByName.apply(table, sortFieldName, true)
}