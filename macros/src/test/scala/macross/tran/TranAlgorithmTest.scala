package macross.tran
import org.testng.annotations._

class TranAlgorithmTest {
  val option = "option"
  val future = "future"
  val value = "value"

  val tranAlgorithm = new TranAlgorithm {
    override type Type = String
    override type ExprTree = String
    override val replaceRule: List[Replace] = List(
      Replace(List(option, option), List(option), (i: ExprTree) => s"$i.flatten"),
      Replace(List(future, future), List(future), (i: ExprTree) => s"$i.flatMap(e=>e)"),
      Replace(List(option, future), List(future, option), (i: ExprTree) => s"$i.traverse")
    )
  }

  import tranAlgorithm._

  val in: TypeList = List(option, future, option, option, value)
  val to: TypeList = List(future, option, option, value)

  @Test(priority = 0)
  def listTriangleTest(): Unit = {
    assert(listTriangle(List(1).map(_.toString)) ==
      List(
        List(1)
      ).map(_.map(_.toString))
    )
    assert(
      listTriangle(List(1, 2, 3, 4).map(_.toString)) ==
        List(
          List(1),
          List(1, 2),
          List(1, 2, 3),
          List(1, 2, 3, 4)
        ).map(_.map(_.toString))
    )
    println(listTriangle(List(1, 2, 3, 4).map(_.toString)))
  }


  @Test(priority = 10)
  def splitListTest(): Unit = {
    assert(splitList(List(1, 2, 3).map(_.toString)) == List(
      (List("1"), List("2", "3")),
      (List("1", "2"), List("3"))
    ))
    println(splitList(List(1, 2, 3).map(_.toString)))
  }

  @Test(priority = 20)
  def exeReplaceTest(): Unit = {
    val rt = exeSingleReplace(in)

    assert(rt.map(e ⇒ e.head ++ e.to) ==
      List(List(future, option, option, option, value))
    )
    println(rt)
  }


  @Test(priority = 30)
  def exeAllReplaceTest(): Unit = {
    val rt = exeAllReplace(in)
    rt.map(_.map(e ⇒ e.head ++ e.to)) == List(
      List(List(future, option, option, option, value)),
      List(List(option, future, option, value)),
      List(List(option, future, option, value))
    )
    println(rt)
  }


  @Test(priority = 40)
  def exeTest(): Unit ={
    def exe(replaceExpr: ReplaceExpr): List[ReplaceExpr] = {
      val ll = exeAllReplace(replaceExpr.typeList)
      if (replaceExpr.typeList == to || ll.isEmpty)
        List(replaceExpr)
      else {
        val replaceExprList = ll.flatten.map(e => {
          type MapFunc = ExprTree ⇒ ExprTree

          val newExprValue = e.head.foldRight(e.replace.func: MapFunc)((r, l) ⇒ {
            (e: ExprTree) ⇒ s"$e.map(e=>${l("e")})"
          })(replaceExpr.exprTree)

          ReplaceExpr(newExprValue, e.head ++ e.to)
        })
        replaceExprList.flatMap(exe)
      }
    }

    val initExpr = "hello"


    val rt = exe(ReplaceExpr(initExpr, in)).find(_.typeList == to)
    assert(rt.nonEmpty)
    println(rt)

  }
}