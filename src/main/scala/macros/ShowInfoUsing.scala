package macros

import macros.annotation.ShowInfo


/**
 * Created by YuJieShui on 2015/9/20.
 */

object ShowInfoUsing {
  trait SuperTrait

  class SuperClass

  @ShowInfo.Show
  //@ShowInfo.showCode
  //@ShowInfo.ShowRaw
  class ShowInfoUsing(val i: Int = 1) extends SuperClass with SuperTrait {
    def f = 1

    val a = 1
  }


}

