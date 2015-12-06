    import scala.reflect.runtime.universe._

    tq"Map[Int,Int]" match {
      case tq"Map[Int,$t2]" => t2
    }

    tq"Map[Int,Seq[Int]]" match {
      case tq"Map[Int,Seq[$t2]]" => t2
    }