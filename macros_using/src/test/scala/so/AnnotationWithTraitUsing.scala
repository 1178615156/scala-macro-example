package so

/**
  * Created by yjs on 2015/12/2.
  */

    trait MyTrait[MT1] {

      def x(t1: MT1)(t2: MT1): MT1 = t1

    }

    trait MyTrait2[MT2] {
      def t(t2: MT2): MT2 = t2
    }


    class MyClass[MCT1, MCT2] extends MyTrait[MCT1] with MyTrait2[MCT2]

    object AnnotationWithTraitUsing extends App {
      assert(new MyClass[Int, String].x(1)(2) == 1)
      assert(new MyClass[Int, String].t("aaa") == "aaa")
    }











