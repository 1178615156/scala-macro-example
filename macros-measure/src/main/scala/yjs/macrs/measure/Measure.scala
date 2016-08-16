package yjs.macrs.measure

import org.slf4j.Logger

import scala.concurrent.Future
import scala.meta.Defn._
import scala.meta._

/**
  * Created by yujieshui on 2016/8/16.
  */
object Measure {

  trait Record[T] {
    def apply(t: => T, method: String): T
  }

  def record[T](t: => T, method: String)(implicit recordImpl: Record[T]): T = recordImpl(t, method)


  //    def applyFuture[T](ft: => Future[T], method: String): Future[T] = {
  //      val startTime = System.nanoTime()
  //      val result = ft
  //      result.onComplete { e =>
  //          val endTime = System.nanoTime()
  //          logger.info(s"method:${(endTime - startTime).toDouble / endTime - startTime}")
  //      }
  //      result
  //    }

  //  final def log(logger: Logger): Record = new Record {
  //    final override def apply[T](t: => T, methodName: String): T = {
  //      val startTime = System.nanoTime()
  //      val result = t
  //      val endTime = System.nanoTime()
  //
  //      logger.info(s"method:${(endTime - startTime).toDouble / endTime - startTime}")
  //      result
  //    }
  //  }

  def impl(any: Any): Tree = any match {
    case x: Def =>
      x.decltpe
      val new_body =
        q"""yjs.macrs.measure.Measure.record(${x.body},${x.name.toString()})"""
      x.copy(body = x.decltpe.map(tpe => q"$new_body:$tpe").getOrElse(new_body))
    case x: Val => x
  }
}

final class Measure extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any) = meta {
    Measure.impl(defn)
  }
}
