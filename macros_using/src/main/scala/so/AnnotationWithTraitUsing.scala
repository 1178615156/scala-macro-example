package so

/**
  * Created by yjs on 2015/12/2.
  */

trait MyTrait[MT] {

  def x(t1: MT)(t2: MT): MT = t1

}


@AnnotationWithTrait
class MyClass[T] extends MyTrait[T]

object AnnotationWithTraitUsing extends App {
  assert(new MyClass[Int].impl.x(1)(2) == 1)
}









