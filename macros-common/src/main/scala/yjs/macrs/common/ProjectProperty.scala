package yjs.macrs.common

import com.typesafe.config.ConfigFactory

/**
  * Created by yuJieShui on 2016/7/15.
  */
trait ProjectProperty {
  val c: scala.reflect.macros.blackbox.Context

  final def config = ConfigFactory.load(getClass.getClassLoader)

  final def projectDir = config.getString("user.dir")

  final def currentPackage = c.internal.enclosingOwner.fullName

}
