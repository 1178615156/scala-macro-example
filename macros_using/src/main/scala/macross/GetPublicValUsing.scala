package macross

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



}
