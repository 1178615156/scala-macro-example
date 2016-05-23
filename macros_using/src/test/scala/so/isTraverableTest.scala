package so

import scala.util.Try

/**
  * Created by yuJieShui on 2016/3/16.
  */
class isTraverableTest {
  val a: {def apply[T](a: T*)} = ???
  isTraverable.apply(
    List(
      List(
        List(1, 2,
          a(3)
        )
      )
    )
  )


}
