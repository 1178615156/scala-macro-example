import scala.collection.generic.FilterMonadic

class A {
  def a = 1
}

val s = Seq(Seq(Seq(Seq(new A, new A, new A), Seq(new A, new A, new A))))
type OutType = Seq[A]

trait TypeClass[X] extends (X ⇒ OutType)

implicit object OutTypeEnd extends TypeClass[OutType] {
  override def apply(v1: OutType): OutType = v1
}

implicit def seq[X]
(implicit recur: TypeClass[X]): TypeClass[Seq[X]] =
  new TypeClass[Seq[X]] {
    override def apply(v1: Seq[X]): OutType = v1.flatMap(recur)
  }

implicit class WithFs[X](val x: X) {
  def fs(implicit make: TypeClass[X]): OutType =
    make.apply(x)
}

s.fs.size
class RecurFlatMap[Out] {

  trait TypeClass[X] extends (X ⇒ Out)

  implicit object End extends TypeClass[Out] {
    override def apply(v1: Out): Out = v1
  }
  implicit def recurImplicit[X,Seq[X]<:FilterMonadic[X,Seq[X]]](implicit recur: TypeClass[X])= new TypeClass[Seq[X]] {
    override def apply(v1: Seq[X]): Out = v1.map(recur)
  }

}

