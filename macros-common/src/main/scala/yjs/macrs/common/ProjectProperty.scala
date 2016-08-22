package yjs.macrs.common

import com.typesafe.config.ConfigFactory

/**
  * Created by yuJieShui on 2016/7/15.
  */
trait ProjectProperty {
  val c: scala.reflect.macros.blackbox.Context

  import c.universe._

  final def config = ConfigFactory.load(getClass.getClassLoader)

  final def projectDir = config.getString("user.dir")

  final def getPackage(symbol: Symbol): String =
    if (symbol.isPackage) symbol.fullName else getPackage(symbol.owner)

  final def currentPackage: String =
    getPackage(c.internal.enclosingOwner)

  final def ownerName = c.internal.enclosingOwner.fullName

}
