import scala.annotation.implicitNotFound

trait Key[T <: Body]

trait Body

case class Body1() extends Body

case class Body2() extends Body

case class Key1() extends Key[Body1]

case class Key2() extends Key[Body2]

@implicitNotFound("keyBody is ${From} != ${To} \n ${new Data()}")
class CompileMsg[From, To]

object CompileMsg {
  implicit def implValue[T] = new CompileMsg[T, T]
}

object Tester {
  def func[KeyBody <: Body, BodyBody <: KeyBody]
  (key: Key[KeyBody], body: BodyBody)
  (implicit msg: CompileMsg[KeyBody, BodyBody]) = {
    println("Key: " + key, "Body: " + body)
  }

  val k1: Key1 = Key1()
  val k2       = Key2()

  val b1        = Body1()
  val b2: Body2 = Body2()
  func(k1, b1) // good match
  func(k1, b2) // wrong match
}

def f (i:Int<:<Int)= i
