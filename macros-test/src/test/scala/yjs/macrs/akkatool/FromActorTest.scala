package yjs.macrs.akkatool

import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.WordSpecLike
import akka.pattern.ask
import akka.testkit.TestKit
import akka.util.Timeout
import yjs.macrs.akkatool.Api.{Hello, HelloResult}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by yujieshui on 2016/11/5.
  */
trait Api {
  def ask(i: Int): Future[String]

  def ask(hello: Hello): Future[HelloResult]
}

object Api {

  case class Hello()

  case class HelloResult()

  implicit val timeout = Timeout(1.second)

  import scala.concurrent.ExecutionContext.Implicits.global

  def fromActor(actorRef: ActorRef): Api = FromActor[Api](actorRef)
}

class FromActorTest extends TestKit(ActorSystem("test")) with WordSpecLike {
  "api" must {
    val api = Api.fromActor(testActor)
    "receive " in {
      api.ask(1)
      expectMsg(1)
      api ask Hello()
      expectMsg(Hello())
    }
  }


}


