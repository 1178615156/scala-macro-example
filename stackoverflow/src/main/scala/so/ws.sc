val l: List[(Option[Set[Long]], Option[Set[(Long, Long)]])] = List((Option(Set(1L, 2L, 3L)), Option(Set((4L, 5L), (6L, 7L)))))

val b =l.map (_._1.getOrElse(Nil))

