package yjs.macrs.akkatool

import akka.actor.{ActorRef, ActorSystem}
import org.scalatest.{BeforeAndAfterAll, Suite, WordSpecLike}
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

  def send(s: String): Unit

  def hello = ask(Hello())
}

object Api {

  case class Hello()

  case class HelloResult()

  implicit val timeout = Timeout(1.second)

  import scala.concurrent.ExecutionContext.Implicits.global

  def fromActor(actorRef: ActorRef): Api = FromActor[Api](actorRef)
}

trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: Suite with TestKit =>
  override protected def afterAll(): Unit = {
    system.terminate()
  }

}

class FromActorTest
  extends TestKit(ActorSystem("test"))
    with WordSpecLike
    with StopSystemAfterAll {

  "api" must {
    val api = Api.fromActor(testActor)
    "ask" in {
      api.ask(1)
      expectMsg(1)
      api ask Hello()
      expectMsg(Hello())
    }
    "send" in {
      api send "123"
      expectMsg("123")
    }
    "hello " in {
      api.hello
      expectMsg(Hello())
    }

  }


}


