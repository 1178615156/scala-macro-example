package macross.slick

import slick.lifted.{Index, ProvenShape}
import SlickDb.api._

/**
  * Created by yu jie shui on 2015/12/2 17:22.
  */
case class SlickStarCaseClassTest(
                                   keyword: List[String],
                                   user_id: Option[String]
                                 )

object SlickStarCaseClassTest {

  class EntityTable(tag: Tag) extends Table[SlickStarCaseClassTest](tag, "aaa") {
    type CCC[T] = Rep[T]
    val keyword: CCC[String] = column[String]("keyword")

    def user_id: Rep[Option[String]] = column[Option[String]]("user_id", O.Length(32))

    val index_user_id: Index = index("index_user_id", user_id)

    implicit def keyword_list2string(s: List[String]) = s.toString()

    implicit def keyword_string2list(i: String) = List(i)


    override def * : ProvenShape[SlickStarCaseClassTest] = SlickStarMacro.apply[EntityTable, SlickStarCaseClassTest]
  }

}
