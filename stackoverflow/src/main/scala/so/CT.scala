package so

import scala.language.experimental.macros
import scala.reflect.macros.whitebox

/**
  * Created by yu jie shui on 2015/12/8 16:40.
  */
object CT {
  def apply: Int = macro CTImpl.apply
}

class CTImpl(val c: whitebox.Context) extends macross.base.ShowInfo {

  import c.universe._


  def apply = {
    val tuple = List(1, 2, 3)
    val caseTuple = q"(..$tuple)"
    val c = List(cq"$caseTuple=>{a}")
    val out = q" 1 match {case ..$c }"


    /*
    1 match {
      case scala.Tuple3(1, 2, 3) => a
    }
     */
    showInfo(show(out))

    q"1"
  }

}



