package macross.tey.slick

/**
  * Created by yu jie shui on 2015/11/8 19:09.
  */
case class User(
                 mobile: String,
                 name: Option[String],
                 id: Long
               )
object UserTable{
  trait Column{

  }
}

class SortWith {

}
