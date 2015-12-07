package so

/**
  * Created by yu jie shui on 2015/12/7 15:47.
  */

//test
object DefWithValueUsing extends App {
  @DefWithValue
  def n: Unit = {


    val waitReplace = "hello"
    val a = ddd

    println(waitReplace)
    assert(waitReplace=="world")
  }
n
}