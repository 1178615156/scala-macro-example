package macros

/**
 * Created by YuJieShui on 2015/9/10.
 */

object GetPublicValUsing extends App {

  object Module_1 {
    val a = 1
    val b = 2
    val c = 3
    //need write return type
    val list: List[Int] = GetPublicValMacros.listValue[Module_1.type, Int]
    val map: Map[String, Int] = GetPublicValMacros.mapValue[Module_1.type, Int]
  }

  println(
    s"""
       |---
       |module1
       |list: ${Module_1.list}
        |map:  ${Module_1.map}
        |---
   """.stripMargin)

  object Module_2 extends base.Enum[String] {
    val a = v2v("aa")
    val b = v2v("bb")
    val c = v2v("cc")


    //need write return type
    val list: List[Module_2.Value] = GetPublicValMacros.listValue[Module_2.type, Value]
    val map: Map[String, Module_2.Value] = GetPublicValMacros.mapValue[Module_2.type, Value]
  }

  println(
    s"""
       |---
       |module2
       |list: ${Module_2.list}
        |map:  ${Module_2.map}
        |---
   """.stripMargin)


}
