package yjs.annotation

/**
  * Created by yuJieShui on 2015/12/21.
  */
object Routes {

  class Post(val url: String) extends scala.annotation.StaticAnnotation

  class Get(val url: String) extends scala.annotation.StaticAnnotation

  class Delete(val url: String) extends scala.annotation.StaticAnnotation

  class Put(val url: String) extends scala.annotation.StaticAnnotation

}
