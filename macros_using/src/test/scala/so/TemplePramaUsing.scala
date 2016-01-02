package so

/**
 * Created by YuJieShui on 2015/10/11.
 */
object TemplePramaUsing extends App {
  println(TemplateParamsMacros.apply[String]("String").t)
}
