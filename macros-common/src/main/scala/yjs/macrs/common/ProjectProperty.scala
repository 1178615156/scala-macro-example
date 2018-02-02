package yjs.macrs.common

import java.nio.file.{Files, Path, Paths}

import scala.collection.JavaConverters._

import com.typesafe.config.ConfigFactory

/**
  * Created by yuJieShui on 2016/7/15.
  */
trait ProjectProperty {
  val c: scala.reflect.macros.blackbox.Context

  import c.universe._

  final def config = ConfigFactory.load(getClass.getClassLoader)

  final def projectDir: String = {
    val userDir = util.Properties.userDir


    def dirExistBuild(p: Path) =
      Files.list(p).iterator().asScala.toList.map(_.getFileName).exists(_.toString == "build.sbt")

    def getProjectDir(p: Path): Path = {
      if(Files.isDirectory(p))
        if(dirExistBuild(p))
          p
        else getProjectDir(p.getParent)
      else
        getProjectDir(p.getParent)
    }

//    println(c.enclosingPosition.source.file.path)

    if(dirExistBuild(Paths.get(userDir)))
      userDir
    else {
      getProjectDir(Paths.get(c.enclosingPosition.source.file.path)).toString
    }
  }

  final def getPackage(symbol: Symbol): String =
    if(symbol.isPackage) symbol.fullName else getPackage(symbol.owner)

  final def currentPackage: String =
    getPackage(c.internal.enclosingOwner)

  final def ownerName = c.internal.enclosingOwner.fullName

}
