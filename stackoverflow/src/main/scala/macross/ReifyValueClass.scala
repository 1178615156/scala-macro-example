//package macross
//
//import scala.language.experimental.macros
//import scala.language.higherKinds
//import scala.reflect.macros.blackbox.Context
//
///**
// * Created by YuJieShui on 2015/10/27.
// */
//class ReifyValueClass(val c: Context) {
//  val a = c.universe.reify {
//    object T {
//      implicit class RicherDouble(val value: Double) extends AnyVal {
//        def times(that: Double): Double = value * that
//      }
//    }
//T
//  }
//}
