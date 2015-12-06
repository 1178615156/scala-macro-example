    import scala.language.existentials
    trait Elem {
      val x: Int
    }
    trait T {
      type E[T] = T
      type ConcreteElem
    }
    trait S {
      self: T =>
      type ConcreteElem =E[Elem]//E[T forSome {type T <: Elem}]
      def f(v: ConcreteElem) = println(v.x)
    }
    new S with T{}  f new Elem {
      override val x: Int = 1
    }