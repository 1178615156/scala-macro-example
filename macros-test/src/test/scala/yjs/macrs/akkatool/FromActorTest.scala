package yjs.macrs.akkatool

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.scalatest.{BeforeAndAfterAll, Suite, WordSpecLike}
import akka.pattern.ask
import akka.testkit.TestKit
import akka.util.Timeout
import com.sun.rowset.internal.InsertRow

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by yujieshui on 2016/11/5.
  */
trait Api {

  import Api._

  def send(s: String): Unit

  def ask(i: Int): Future[String]

  def ask(hello: Hello): Future[HelloResult]

  def hello = ask(Hello())

  def insert(i: Insert): Future[InsetResult]
}

object Api {

  case class Insert(i: Int)

  case class InsetResult(result: Boolean)

  case class Hello()

  case class HelloResult()

  implicit val timeout = Timeout(1.second)

  import scala.concurrent.ExecutionContext.Implicits.global

  def fromActor(actorRef: ActorRef): Api = TypeSafeActor.fromActor[Api](actorRef)
}

import Api._

class ApiActor extends Actor with Api {

  import context.dispatcher

  override def ask(i: Int): Future[String] = {
    Future.successful(i.toString)
  }

  override def ask(hello: Hello): Future[HelloResult] = Future.successful(HelloResult())

  override def send(s: String): Unit = ()

  override def insert(i: Insert): Future[InsetResult] = Future.successful(InsetResult(true))

  override def receive: Receive = TypeSafeActor.receive[Api](this)
}


trait StopSystemAfterAll extends BeforeAndAfterAll {
  this: Suite with TestKit =>
  override protected def afterAll(): Unit = {
    system.terminate()
  }

}

class TypeSafeActorTest
  extends TestKit(ActorSystem("test"))
    with WordSpecLike
    with StopSystemAfterAll {

  "fromActor" must {
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


  "receive" must {
    val actor = system.actorOf(Props(new ApiActor))
    val api = Api.fromActor(actor)
    "ask" in {
      assert(Await.result(api.ask(1), Duration.Inf) === "1")
      assert(Await.result(api.ask(2), Duration.Inf) === "2")
      assert(Await.result(api.ask(Hello()), Duration.Inf) === HelloResult())
      assert(Await.result(api.insert(Insert(11)), Duration.Inf) === InsetResult(true))
    }
  }


}


