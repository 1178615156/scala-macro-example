package yjs.macrs.play

/**
  * Created by yuJieShui on 2016/7/15.
  */
object Routes {

  class Path(val path: String) extends scala.annotation.StaticAnnotation

  trait Method

  class Get(val url: String) extends scala.annotation.StaticAnnotation with Method

  class Delete(val url: String) extends scala.annotation.StaticAnnotation with Method

  class Put(val url: String) extends scala.annotation.StaticAnnotation with Method

  class Post(val url: String) extends scala.annotation.StaticAnnotation with Method

}
