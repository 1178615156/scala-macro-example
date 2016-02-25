package macross.tran2

/**
  * Created by yjs on 2016/1/26.
  */
object A {
  implicit val tranRule  = new TranRule {
  }
  implicit val tranRule2 = new TranRule {
  }
}

object TranToUsing extends App {

  import A.tranRule

  TranToMacros.apply[List[List[Int]], List[Int]]
}
