package yjs.macrs.play

/**
  * Created by yuJieShui on 2016/7/15.
  */
object Routes {

  class Post(val url: String) extends scala.annotation.StaticAnnotation

  class Get(val url: String) extends scala.annotation.StaticAnnotation

  class Delete(val url: String) extends scala.annotation.StaticAnnotation

  class Put(val url: String) extends scala.annotation.StaticAnnotation

  class Path(val path: String) extends scala.annotation.StaticAnnotation

}
