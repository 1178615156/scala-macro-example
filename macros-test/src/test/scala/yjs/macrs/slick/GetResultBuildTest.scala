package yjs.macrs.slick

/**
  * Created by yujieshui on 2017/5/12.
  */
case class Entity(id: Int, name: String, helloWorld: Option[String])

class GetResultBuildTest {
  GetResultBuild.literal[Entity]

}
