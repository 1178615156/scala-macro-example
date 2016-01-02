package so

/**
  * Created by yjs on 2015/12/1.
  */
trait AA{
  def aa=1
}








trait BB

trait CC

@SuperClass
class SuperClassUsing extends AA with BB with CC//when show info List(AA,BB,CC)