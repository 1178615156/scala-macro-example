package macross.base

import scala.util.Try

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
trait ProjectFolder extends ShowInfo {
  val c: Context

  def config = ConfigFactory.load(getClass.getClassLoader)

  def rootProjectDir = ConfigFactory.load().getString("user.dir")

  def projectDir = config.getString("user.dir")

  object Play {
    val DefaultOutputDir  = "conf"
    val OutputDirSettings = "conf.output.dir="

    lazy val rootFolder = new File("").getParentFile

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
