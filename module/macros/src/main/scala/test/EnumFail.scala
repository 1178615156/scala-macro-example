package test

/**
 * Created by YuJieShui on 2015/10/13.
 */
sealed trait EnumFail {
  val id: Int
}

object EnumFail {

  case object A extends EnumFail {
    val id = 1
  }

  case object B extends EnumFail {
    val id = 2
  }

  case object C extends EnumFail {
    val id = 2
  }

}



sealed trait EnumPass {
  val id: Int
}

object EnumPass {

  case object A extends EnumPass {
    val id = 1
  }

  case object B extends EnumPass {
    val id = 2
  }

  case object C extends EnumPass {
    val id = 3
  }

}