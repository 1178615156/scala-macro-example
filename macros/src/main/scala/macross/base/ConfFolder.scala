package macross.base

import java.io.File

import com.typesafe.config.ConfigFactory

import scala.language.experimental.macros
import scala.reflect.macros.blackbox.Context

/**
  * the idea from wacai/config-annotation
  * [[https://github.com/wacai/config-annotation/blob/master/src/main/scala/com/wacai/config/annotation/Macro.scala]]
  *
  * Created by yjs on 2015/12/13.
  */
trait ConfFolder {
  val c: Context
  lazy val config = ConfigFactory.load()

  object Play {
    val DefaultOutputDir  = "conf"
    val OutputDirSettings = "conf.output.dir="

    lazy val confOutputDir = {
      val f = new File(c.settings
        .find(_.startsWith(OutputDirSettings))
        .map(_.substring(OutputDirSettings.length))
        .getOrElse(DefaultOutputDir))

      if (!f.exists()) f.mkdirs()
      f
    }
  }

}
